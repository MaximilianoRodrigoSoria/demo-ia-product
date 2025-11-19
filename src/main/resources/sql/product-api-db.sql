-- product-api-db.sql
-- Propósito: Crear la tabla "product" con campos comunes, índices, trigger para mantener "updated_at" y un registro de ejemplo.
-- Nota: La sección principal está pensada para PostgreSQL. Al final hay una variante comentada para H2 (uso en tests / desarrollo embebido).

-- ===========================================
-- PostgreSQL: DDL principal
-- ===========================================

CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(14,2) NOT NULL CHECK (price >= 0),
    currency CHAR(3) NOT NULL DEFAULT 'USD',
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

-- Índices sugeridos para consultas frecuentes
CREATE INDEX IF NOT EXISTS idx_product_name ON product (name);
CREATE INDEX IF NOT EXISTS idx_product_created_at ON product (created_at);

-- Trigger: actualizar 'updated_at' automáticamente en UPDATE
-- Requiere privilegios para crear funciones en la base.
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_product_updated_at ON product;
CREATE TRIGGER trg_product_updated_at
BEFORE UPDATE ON product
FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Ejemplo de inserción
INSERT INTO product (sku, name, description, price, currency, stock, active)
VALUES ('SKU-001', 'Producto de ejemplo', 'Descripción inicial de producto', 199.90, 'ARS', 100, true);

-- ===========================================
-- Variante: H2 (embedded) - copiar/ajustar si usa H2 en desarrollo/tests
-- Comentada: descomentar si corresponde y comentar la sección PostgreSQL anterior.
-- ===========================================
--
-- CREATE TABLE IF NOT EXISTS product (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     sku VARCHAR(50) NOT NULL UNIQUE,
--     name VARCHAR(255) NOT NULL,
--     description CLOB,
--     price DECIMAL(14,2) NOT NULL,
--     currency CHAR(3) NOT NULL DEFAULT 'USD',
--     stock INT NOT NULL DEFAULT 0,
--     active BOOLEAN NOT NULL DEFAULT TRUE,
--     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     version BIGINT NOT NULL DEFAULT 0
-- );
--
-- -- H2 no soporta triggers plpgsql; se puede usar una expresión en la aplicación
-- -- para mantener updated_at o un trigger H2 específico si se requiere.
--
-- INSERT INTO product (sku, name, description, price, currency, stock, active)
-- VALUES ('SKU-001', 'Producto de ejemplo', 'Descripción inicial de producto', 199.90, 'ARS', 100, TRUE);
--
-- ===========================================
-- Notas:
-- - Ajustar el tipo/precisión de `price` según requerimientos financieros (escala/precisión).
-- - `version` puede mapearse a @Version de JPA para control optimista desde la aplicación.
-- - No se incluyen campos sensibles. Indexar columnas según patrones de consulta reales.
-- - Revisar permisos necesarios para la creación de funciones/triggers en Postgres.
-- ===========================================


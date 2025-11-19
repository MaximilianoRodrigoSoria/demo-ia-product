-- product-data.sql
-- Propósito: Insertar datos de ejemplo en la tabla product
-- Este script se ejecuta después de product-api-db.sql

-- Limpiar datos existentes (opcional, comentar si no se desea)
-- DELETE FROM product WHERE sku NOT IN ('SKU-001');

-- Insertar productos de ejemplo
INSERT INTO product (sku, name, description, price, currency, stock, active) VALUES
('SKU-002', 'Laptop Dell XPS 13', 'Laptop ultraportátil de 13 pulgadas con procesador Intel Core i7, 16GB RAM, 512GB SSD', 1299.99, 'USD', 15, true),
('SKU-003', 'Mouse Logitech MX Master 3', 'Mouse inalámbrico ergonómico con sensor de alta precisión y batería de larga duración', 99.99, 'USD', 50, true),
('SKU-004', 'Teclado Mecánico Keychron K8', 'Teclado mecánico inalámbrico RGB con switches hot-swappable', 89.99, 'USD', 30, true),
('SKU-005', 'Monitor Samsung 27" 4K', 'Monitor profesional 4K UHD de 27 pulgadas con panel IPS y HDR10', 449.99, 'USD', 20, true),
('SKU-006', 'Auriculares Sony WH-1000XM5', 'Auriculares inalámbricos con cancelación de ruido premium', 399.99, 'USD', 25, true),
('SKU-007', 'SSD Samsung 980 PRO 1TB', 'Unidad SSD NVMe Gen 4 de alto rendimiento', 149.99, 'USD', 40, true),
('SKU-008', 'Webcam Logitech C920', 'Cámara web Full HD 1080p con micrófono estéreo integrado', 79.99, 'USD', 35, true),
('SKU-009', 'Hub USB-C Anker 7 en 1', 'Hub multipuerto con HDMI, USB 3.0, lector de tarjetas SD y carga PD', 49.99, 'USD', 60, true),
('SKU-010', 'Soporte Laptop Rain Design mStand', 'Soporte de aluminio para laptop con diseño ergonómico', 39.99, 'USD', 45, true),
('SKU-011', 'Cable USB-C Thunderbolt 4', 'Cable certificado Thunderbolt 4 de 1 metro, 40Gbps', 29.99, 'USD', 100, true),
('SKU-012', 'Mousepad Extended Razer Gigantus', 'Mousepad gaming de tamaño extendido con superficie de tela', 24.99, 'USD', 70, true),
('SKU-013', 'Silla Ergonómica Herman Miller Aeron', 'Silla de oficina ergonómica premium con soporte lumbar ajustable', 1395.00, 'USD', 8, true),
('SKU-014', 'Escritorio Uplift V2', 'Escritorio de pie eléctrico ajustable en altura', 799.00, 'USD', 12, true),
('SKU-015', 'Lámpara LED BenQ ScreenBar', 'Lámpara LED para monitor con control automático de brillo', 109.99, 'USD', 28, true),
('SKU-016', 'Micrófono Blue Yeti', 'Micrófono USB profesional para streaming y podcasting', 129.99, 'USD', 22, true),
('SKU-017', 'Router WiFi 6 TP-Link AX3000', 'Router inalámbrico de doble banda con WiFi 6', 99.99, 'USD', 18, true),
('SKU-018', 'Disco Duro Externo WD 4TB', 'Disco duro portátil USB 3.0 de 4TB', 89.99, 'USD', 55, true),
('SKU-019', 'Impresora HP LaserJet Pro', 'Impresora láser monocromática con WiFi', 199.99, 'USD', 10, true),
('SKU-020', 'Tablet iPad Air 5ta Gen', 'Tablet de 10.9 pulgadas con chip M1, 64GB', 599.99, 'USD', 14, true),
('SKU-021', 'Smartwatch Apple Watch Series 9', 'Reloj inteligente con GPS, pantalla siempre activa', 429.99, 'USD', 16, true);

-- Algunos productos inactivos o con stock bajo
INSERT INTO product (sku, name, description, price, currency, stock, active) VALUES
('SKU-022', 'Producto Descontinuado', 'Este producto ya no está disponible', 0.00, 'USD', 0, false),
('SKU-023', 'Mouse Inalámbrico Básico', 'Mouse inalámbrico de entrada con bajo stock', 19.99, 'USD', 3, true),
('SKU-024', 'Teclado Antiguo', 'Modelo anterior descatalogado', 29.99, 'USD', 0, false);

-- Productos en diferentes monedas (ARS - Pesos Argentinos)
INSERT INTO product (sku, name, description, price, currency, stock, active) VALUES
('SKU-025', 'Notebook Lenovo IdeaPad 3', 'Laptop económica con procesador AMD Ryzen 5', 89990.00, 'ARS', 25, true),
('SKU-026', 'Mouse Genius DX-110', 'Mouse USB básico para oficina', 2499.00, 'ARS', 80, true),
('SKU-027', 'Parlantes Genius SP-HF160', 'Parlantes estéreo 2.0 con conexión USB', 4999.00, 'ARS', 40, true),
('SKU-028', 'Pendrive Kingston 64GB', 'Memoria USB 3.0 de 64GB', 3499.00, 'ARS', 120, true),
('SKU-029', 'Cable HDMI 2 metros', 'Cable HDMI 2.0 de alta velocidad', 1999.00, 'ARS', 95, true),
('SKU-030', 'Webcam HD 720p Genérica', 'Cámara web HD económica con micrófono', 5999.00, 'ARS', 32, true);

-- Confirmar inserción exitosa
SELECT COUNT(*) as total_productos FROM product;

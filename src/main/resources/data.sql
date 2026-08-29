INSERT INTO categories (name)
SELECT '생활'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '생활');

INSERT INTO categories (name)
SELECT '맛집'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '맛집');

INSERT INTO categories (name)
SELECT '안전'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '안전');

INSERT INTO categories (name)
SELECT '교통'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '교통');

INSERT INTO categories (name)
SELECT '기타'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '기타');

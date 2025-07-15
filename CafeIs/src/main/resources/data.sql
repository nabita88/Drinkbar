USE testdb;


-- 0. 지점(매장) 테이블
CREATE TABLE tbl_branch (
                            branch_no BIGINT AUTO_INCREMENT PRIMARY KEY,
                            branch_title VARCHAR(100) NOT NULL,
                            location_text VARCHAR(200) NOT NULL,
                            seat_count INT DEFAULT 0,
                            open_schedule_json JSON,
                            operating_status VARCHAR(30) DEFAULT 'OPEN',
                            operation_note VARCHAR(200),
                            INDEX idx_branch_status (operating_status),
                            INDEX idx_branch_title (branch_title),
                            INDEX idx_branch_text (location_text),
                            INDEX idx_branch_status_title (operating_status, branch_title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



-- 1. 등록자(회원) 테이블
CREATE TABLE tbl_registrant (
                                email VARCHAR(100) NOT NULL PRIMARY KEY,
                                pw VARCHAR(100) NOT NULL,
                                nickname VARCHAR(100) NOT NULL,

                                INDEX idx_registrant_nickname (nickname),
                                INDEX idx_registrant_email_pw (email, pw)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 1-1. 등록자 역할 테이블 (ElementCollection)
CREATE TABLE tbl_registrant_role (
                                     registrant_email VARCHAR(100) NOT NULL,
                                     role VARCHAR(20) NOT NULL,
                                     PRIMARY KEY (registrant_email, role),
                                     FOREIGN KEY (registrant_email) REFERENCES tbl_registrant(email),

                                     INDEX idx_registrant_role_email (registrant_email),
                                     INDEX idx_registrant_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 상품 테이블
CREATE TABLE tbl_product (
                             product_no BIGINT AUTO_INCREMENT PRIMARY KEY,
                             product_name VARCHAR(100) NOT NULL,
                             selling_price INT NOT NULL,
                             description VARCHAR(500),
                             is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
                             category VARCHAR(100) NOT NULL,
                             branch_no BIGINT,
                             servable_count INT DEFAULT 0 NOT NULL,
                             dimension_large BOOLEAN DEFAULT FALSE,
                             dimension_mega BOOLEAN DEFAULT FALSE,
                             thermal_cold BOOLEAN DEFAULT FALSE,
                             thermal_hot BOOLEAN DEFAULT FALSE,
                             last_updated TIMESTAMP,
                             FOREIGN KEY (branch_no) REFERENCES tbl_branch(branch_no),
                             INDEX idx_product_store (branch_no),
                             INDEX idx_product_category (category),
                             INDEX idx_product_deleted (is_deleted),
                             INDEX idx_product_name (product_name),
                             INDEX idx_product_price (selling_price),
                             INDEX idx_product_branch_category (branch_no, category),
                             INDEX idx_product_branch_deleted (branch_no, is_deleted),
                             INDEX idx_product_category_deleted (category, is_deleted),
                             INDEX idx_product_branch_category_deleted (branch_no, category, is_deleted),
                             INDEX idx_product_updated (last_updated)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 상품 이미지 테이블
CREATE TABLE tbl_product_visual_content (
                                            product_product_no BIGINT NOT NULL,
                                            image_file_full_name VARCHAR(255),
                                            FOREIGN KEY (product_product_no) REFERENCES tbl_product(product_no),
                                            INDEX (product_product_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 장바구니 테이블
CREATE TABLE tbl_basket (
                            basket_no BIGINT AUTO_INCREMENT PRIMARY KEY,
                            registrant VARCHAR(100) NOT NULL,
                            branch_no BIGINT NOT NULL,
                            FOREIGN KEY (registrant) REFERENCES tbl_registrant(email),
                            FOREIGN KEY (branch_no) REFERENCES tbl_branch(branch_no),
                            UNIQUE KEY uk_basket_registrant_store (registrant, branch_no),
                            INDEX idx_basket_email (registrant),
                            INDEX idx_basket_branch (branch_no),
                            INDEX idx_basket_registrant_branch (registrant, branch_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. 장바구니 상품 테이블
CREATE TABLE tbl_basket_goods (
                                  basket_goods_no BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  basket_no BIGINT NOT NULL,
                                  product_no BIGINT NOT NULL,
                                  quantity INT DEFAULT 1 NOT NULL,
                                  size VARCHAR(20),
                                  temperature VARCHAR(20),
                                  additional_options VARCHAR(500),
                                  additional_price INT DEFAULT 0,
                                  created_at TIMESTAMP,
                                  FOREIGN KEY (basket_no) REFERENCES tbl_basket(basket_no),
                                  FOREIGN KEY (product_no) REFERENCES tbl_product(product_no),
                                  INDEX idx_basket_goods (product_no, basket_no),
                                  INDEX idx_basket_goods_created (created_at),
                                  INDEX idx_basket_goods_basket (basket_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. 주문 테이블
CREATE TABLE tbl_order (
                           order_no BIGINT AUTO_INCREMENT PRIMARY KEY,
                           registrant_email VARCHAR(100) NOT NULL,
                           branch_no BIGINT NOT NULL,
                           order_time TIMESTAMP NOT NULL,
                           order_date DATE NOT NULL,
                           requested_pickup_time TIMESTAMP,
                           confirmed_pickup_time TIMESTAMP,
                           order_status VARCHAR(20) DEFAULT 'PENDINGENTRY' NOT NULL,
                           total_amount INT DEFAULT 0 NOT NULL,
                           discount_amount INT DEFAULT 0 NOT NULL,
                           final_amount INT DEFAULT 0 NOT NULL,
                           cash_paid BOOLEAN DEFAULT FALSE NOT NULL,
                           cash_payment_date TIMESTAMP,
                           special_requests TEXT,
                           termination_type VARCHAR(20),
                           termination_reason VARCHAR(500),
                           termination_date TIMESTAMP,
                           received_date TIMESTAMP,
                           FOREIGN KEY (registrant_email) REFERENCES tbl_registrant(email),
                           FOREIGN KEY (branch_no) REFERENCES tbl_branch(branch_no),
                           INDEX idx_order_registrant (registrant_email),
                           INDEX idx_order_store (branch_no),
                           INDEX idx_order_status (order_status),
                           INDEX idx_order_date (order_date),
                           INDEX idx_order_time (order_time),
                           INDEX idx_pickup_time (confirmed_pickup_time),
                           INDEX idx_order_registrant_status (registrant_email, order_status),
                           INDEX idx_order_registrant_date (registrant_email, order_date),
                           INDEX idx_order_store_status (branch_no, order_status),
                           INDEX idx_order_cash_paid (cash_paid),
                           INDEX idx_order_status_date (order_status, order_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. 주문 상품 테이블
CREATE TABLE tbl_order_item (
                                order_item_no BIGINT AUTO_INCREMENT PRIMARY KEY,
                                order_no BIGINT NOT NULL,
                                product_no BIGINT NOT NULL,
                                product_name VARCHAR(200) NOT NULL,
                                price INT NOT NULL,
                                quantity INT DEFAULT 1 NOT NULL,
                                size VARCHAR(20),
                                temperature VARCHAR(20),
                                additional_options VARCHAR(500),
                                additional_price INT DEFAULT 0,
                                product_image VARCHAR(300),
                                item_status VARCHAR(20) DEFAULT 'NORMAL' NOT NULL,
                                created_at TIMESTAMP,
                                updated_at TIMESTAMP,
                                FOREIGN KEY (order_no) REFERENCES tbl_order(order_no),
                                FOREIGN KEY (product_no) REFERENCES tbl_product(product_no),
                                INDEX idx_orderitem_order (order_no),
                                INDEX idx_orderitem_product (product_no),
                                INDEX idx_orderitem_status (item_status),
                                INDEX idx_orderitem_order_status (order_no, item_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


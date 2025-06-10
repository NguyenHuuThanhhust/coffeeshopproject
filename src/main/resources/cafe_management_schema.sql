-- =========================================================================================
-- COFFEE SHOP MANAGEMENT DATABASE - FULL SQL (KHÔNG DỮ LIỆU MẪU)
-- ĐÃ TÍCH HỢP HỆ THỐNG PHÂN QUYỀN BẰNG VAI TRÒ (KHÔNG MẬT KHẨU)
-- =========================================================================================

-- START: DROP TABLES IN REVERSE ORDER TO AVOID FOREIGN KEY CONSTRAINTS
DROP TABLE IF EXISTS ORDER_DETAIL CASCADE;
DROP TABLE IF EXISTS CUSTOMER_ORDER CASCADE;
DROP TABLE IF EXISTS MENU_ITEM CASCADE;
DROP TABLE IF EXISTS EVENT_PROMOTION CASCADE;
DROP TABLE IF EXISTS PURCHASE_ORDER_DETAIL CASCADE;
DROP TABLE IF EXISTS PURCHASE_ORDER CASCADE;
DROP TABLE IF EXISTS INGREDIENT CASCADE;
DROP TABLE IF EXISTS SUPPLIER CASCADE;
DROP TABLE IF EXISTS EMPLOYEE CASCADE;
DROP TABLE IF EXISTS CUSTOMER CASCADE;
DROP TABLE IF EXISTS MEMBERSHIP_RANK CASCADE;
-- Bảng ROLE_PASSWORDS bị loại bỏ
DROP TABLE IF EXISTS ROLE_PASSWORDS CASCADE;
DROP TABLE IF EXISTS ROLES CASCADE;
-- END: DROP TABLES

---
--- KHÔNG CẦN CÀI ĐẶT EXTENSION pgcrypto NỮA KHI KHÔNG CÓ MẬT KHẨU
---

-- =============================================
-- 0. ROLES TABLE (Bảng Vai trò)
-- =============================================
CREATE TABLE ROLES (
                       RoleName VARCHAR(50) PRIMARY KEY
);

-- Dữ liệu cấu hình cho các vai trò
INSERT INTO ROLES (RoleName) VALUES ('Staff'), ('Manager')
    ON CONFLICT (RoleName) DO NOTHING;

-- Bảng ROLE_PASSWORDS đã bị loại bỏ

-- =============================================
-- 1. MEMBERSHIP_RANK TABLE (Hạng thành viên)
-- =============================================
CREATE TABLE MEMBERSHIP_RANK (
                                 RankName VARCHAR(50) PRIMARY KEY,
                                 PointFrom NUMERIC(15,2) NOT NULL,
                                 PointTo NUMERIC(15,2) NOT NULL,
                                 DiscountRate NUMERIC(5,2) DEFAULT 0,
                                 LoyaltyPointRate NUMERIC(5,2) DEFAULT 0
);

INSERT INTO MEMBERSHIP_RANK (RankName, PointFrom, PointTo, DiscountRate, LoyaltyPointRate)
VALUES
    ('Bronze', 0, 1999999.99, 0.00, 0),
    ('Silver', 2000000.00, 4999999.99, 0.05, 0),
    ('Gold', 5000000.00, 8999999.99, 0.10, 0),
    ('Platinum', 9000000.00, 15000000.00, 0.15, 0)
    ON CONFLICT (RankName) DO UPDATE SET
    PointFrom = EXCLUDED.PointFrom,
                                  PointTo = EXCLUDED.PointTo,
                                  DiscountRate = EXCLUDED.DiscountRate,
                                  LoyaltyPointRate = EXCLUDED.LoyaltyPointRate;


-- =============================================
-- 2. CUSTOMER TABLE (Khách hàng)
-- =============================================
CREATE TABLE CUSTOMER (
                          CustomerID SERIAL PRIMARY KEY,
                          CustomerName VARCHAR(100) NOT NULL,
                          PhoneNumber VARCHAR(20) UNIQUE NOT NULL,
                          Email VARCHAR(100) UNIQUE,
                          DateJoined DATE DEFAULT CURRENT_DATE,
                          TotalSpent NUMERIC(15, 2) DEFAULT 0,
                          Rank VARCHAR(50) DEFAULT 'Bronze',
                          FOREIGN KEY (Rank) REFERENCES MEMBERSHIP_RANK(RankName)
);

-- =============================================
-- 3. EMPLOYEE TABLE (Nhân viên)
-- =============================================
CREATE TABLE EMPLOYEE (
                          EmployeeID SERIAL PRIMARY KEY,
                          EmployeeName VARCHAR(100) NOT NULL,
                          Position VARCHAR(50),
                          PhoneNumber VARCHAR(20),
                          Email VARCHAR(100) UNIQUE,
                          StartDate DATE,
                          HourlyWage NUMERIC(10,2) DEFAULT 0
);

-- =============================================
-- 4. MENU_ITEM TABLE (Món trong thực đơn)
-- =============================================
CREATE TABLE MENU_ITEM (
                           MenuItemID SERIAL PRIMARY KEY,
                           ItemName VARCHAR(100) NOT NULL,
                           Description TEXT,
                           Price NUMERIC(10, 2) NOT NULL,
                           Category VARCHAR(50),
                           Status VARCHAR(20) DEFAULT 'Available',
                           IsAvailable BOOLEAN DEFAULT TRUE,
                           ImageURL VARCHAR(255),
                           Thumbnail BYTEA,
                           MinSellingPrice NUMERIC(10,2) DEFAULT 0
);

-- =============================================
-- 5. EVENT_PROMOTION TABLE (Khuyến mãi sự kiện)
-- =============================================
CREATE TABLE EVENT_PROMOTION (
                                 PromotionID SERIAL PRIMARY KEY,
                                 PromotionName VARCHAR(100) NOT NULL UNIQUE,
                                 Description TEXT,
                                 PromotionType VARCHAR(50),
                                 Value NUMERIC(10,2),
                                 RemainingUses INT DEFAULT 0,
                                 StartDate DATE,
                                 EndDate DATE,
                                 MinOrderAmount NUMERIC(10,2) DEFAULT 0
);

-- =============================================
-- 6. CUSTOMER_ORDER TABLE (Đơn hàng của khách hàng)
-- =============================================
CREATE TABLE CUSTOMER_ORDER (
                                OrderID SERIAL PRIMARY KEY,
                                CustomerID INT REFERENCES CUSTOMER(CustomerID) ON DELETE SET NULL,
                                EmployeeID INT REFERENCES EMPLOYEE(EmployeeID) ON DELETE SET NULL,
                                PromotionID INT REFERENCES EVENT_PROMOTION(PromotionID),
                                OrderTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                ExpectedPickupTime TIMESTAMP,
                                TotalAmount NUMERIC(10, 2) DEFAULT 0,
                                TotalAmountUSD NUMERIC(10, 2) DEFAULT 0,
                                ExchangeRate NUMERIC(10, 4) DEFAULT 25000,
                                RankDiscount NUMERIC(10,2) DEFAULT 0,
                                PromotionDiscount NUMERIC(10,2) DEFAULT 0,
                                OrderStatus VARCHAR(30) DEFAULT 'Pending',
                                PaymentMethod VARCHAR(50),
                                IsPaid BOOLEAN DEFAULT FALSE,
                                Notes TEXT
);

-- =============================================
-- 7. ORDER_DETAIL TABLE (Chi tiết đơn hàng)
-- =============================================
CREATE TABLE ORDER_DETAIL (
                              OrderDetailID SERIAL PRIMARY KEY,
                              OrderID INT NOT NULL REFERENCES CUSTOMER_ORDER(OrderID) ON DELETE CASCADE,
                              MenuItemID INT NOT NULL REFERENCES MENU_ITEM(MenuItemID),
                              Quantity INT NOT NULL,
                              UnitPrice NUMERIC(10, 2) NOT NULL
);

-- =============================================
-- CÁC BẢNG QUẢN LÝ TỒN KHO NGUYÊN LIỆU
-- =============================================

-- 8. SUPPLIER TABLE (Nhà cung cấp)
CREATE TABLE SUPPLIER (
                          SupplierID SERIAL PRIMARY KEY,
                          SupplierName VARCHAR(100) NOT NULL UNIQUE,
                          ContactPerson VARCHAR(100),
                          PhoneNumber VARCHAR(20),
                          Email VARCHAR(100),
                          Address TEXT
);

-- 9. INGREDIENT TABLE (Nguyên liệu / Tồn kho)
CREATE TABLE INGREDIENT (
                            IngredientID SERIAL PRIMARY KEY,
                            IngredientName VARCHAR(100) NOT NULL UNIQUE,
                            UnitOfMeasure VARCHAR(20) NOT NULL,
                            CurrentStock NUMERIC(10,2) DEFAULT 0 NOT NULL,
                            MinStockLevel NUMERIC(10,2) DEFAULT 0,
                            LastRestockDate DATE
);

INSERT INTO INGREDIENT (IngredientName, UnitOfMeasure, CurrentStock, MinStockLevel)
VALUES ('Cốc/Ly Tiêu Chuẩn', 'cái', 0, 100)
    ON CONFLICT (IngredientName) DO NOTHING;


-- 10. PURCHASE_ORDER TABLE (Đơn đặt hàng nhập kho)
CREATE TABLE PURCHASE_ORDER (
                                PurchaseOrderID SERIAL PRIMARY KEY,
                                SupplierID INT NOT NULL REFERENCES SUPPLIER(SupplierID),
                                OrderDate DATE DEFAULT CURRENT_DATE,
                                ExpectedDeliveryDate DATE,
                                ActualDeliveryDate DATE,
                                TotalAmount NUMERIC(10,2) DEFAULT 0,
                                OrderStatus VARCHAR(30) DEFAULT 'Pending'
);

-- 11. PURCHASE_ORDER_DETAIL TABLE (Chi tiết đơn đặt hàng nhập kho)
CREATE TABLE PURCHASE_ORDER_DETAIL (
                                       PurchaseOrderDetailID SERIAL PRIMARY KEY,
                                       PurchaseOrderID INT NOT NULL REFERENCES PURCHASE_ORDER(PurchaseOrderID) ON DELETE CASCADE,
                                       IngredientID INT NOT NULL REFERENCES INGREDIENT(IngredientID),
                                       QuantityOrdered NUMERIC(10,2) NOT NULL,
                                       UnitPrice NUMERIC(10,2) NOT NULL,
                                       UNIQUE (PurchaseOrderID, IngredientID)
);


-- =============================================
-- CÁC HÀM VÀ TRIGGER
-- =============================================

-- 1. FUNCTION & TRIGGER: Cập nhật hạng thành viên tự động (trg_auto_rank_update)
CREATE OR REPLACE FUNCTION trg_update_rank()
RETURNS TRIGGER AS $$
DECLARE
new_rank_name VARCHAR(50);
BEGIN
SELECT RankName INTO new_rank_name
FROM MEMBERSHIP_RANK
WHERE NEW.TotalSpent BETWEEN PointFrom AND PointTo
ORDER BY PointFrom DESC
    LIMIT 1;

IF new_rank_name IS NOT NULL AND NEW.Rank IS DISTINCT FROM new_rank_name THEN
        NEW.Rank := new_rank_name;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_auto_rank_update
BEFORE UPDATE ON CUSTOMER
                     FOR EACH ROW
                     WHEN (NEW.TotalSpent IS DISTINCT FROM OLD.TotalSpent)
                     EXECUTE FUNCTION trg_update_rank();


-- 2. FUNCTION & TRIGGER: Kiểm tra khuyến mãi trước khi chèn CUSTOMER_ORDER (trg_check_promo_before_insert)
CREATE OR REPLACE FUNCTION trg_check_promo_usage()
RETURNS TRIGGER AS $$
DECLARE
promo RECORD;
BEGIN
    IF NEW.PromotionID IS NOT NULL THEN
SELECT * INTO promo FROM EVENT_PROMOTION
WHERE PromotionID = NEW.PromotionID;

IF NOT FOUND THEN
            RAISE EXCEPTION 'Promotion with ID % does not exist.', NEW.PromotionID;
        ELSIF promo.RemainingUses IS NOT NULL AND promo.RemainingUses <= 0 THEN
            RAISE EXCEPTION 'Promotion "%" has no remaining uses.', promo.PromotionName;
        ELSIF CURRENT_DATE NOT BETWEEN promo.StartDate AND promo.EndDate THEN
            RAISE EXCEPTION 'Promotion "%" is not currently valid (active from % to %).', promo.PromotionName, promo.StartDate, promo.EndDate;
END IF;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_promo_before_insert
    BEFORE INSERT ON CUSTOMER_ORDER
    FOR EACH ROW
    EXECUTE FUNCTION trg_check_promo_usage();


-- 3. FUNCTION & TRIGGER: Giảm số lượt sử dụng khuyến mãi khi đơn hàng hoàn thành (trg_reduce_promo_after_update)
CREATE OR REPLACE FUNCTION trg_reduce_promo_usage()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.OrderStatus <> 'Completed'
       AND NEW.OrderStatus = 'Completed'
       AND NEW.PromotionID IS NOT NULL THEN
UPDATE EVENT_PROMOTION
SET RemainingUses = RemainingUses - 1
WHERE PromotionID = NEW.PromotionID;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_reduce_promo_after_update
    AFTER UPDATE ON CUSTOMER_ORDER
    FOR EACH ROW
    EXECUTE FUNCTION trg_reduce_promo_usage();


-- 4. FUNCTION & TRIGGER: Đặt giá đơn vị cho chi tiết đơn hàng từ MENU_ITEM (trg_set_unitprice_on_insert)
CREATE OR REPLACE FUNCTION trg_set_order_detail_unitprice()
RETURNS TRIGGER AS $$
DECLARE
item_status VARCHAR(20);
    item_is_available BOOLEAN;
BEGIN
SELECT Price, Status, IsAvailable INTO NEW.UnitPrice, item_status, item_is_available
FROM MENU_ITEM
WHERE MenuItemID = NEW.MenuItemID;

IF NOT FOUND THEN
        RAISE EXCEPTION 'MenuItemID % not found in MENU_ITEM table.', NEW.MenuItemID;
    ELSIF item_is_available = FALSE OR item_status = 'Unavailable' THEN
        RAISE EXCEPTION 'Menu item "%" (ID: %) is currently unavailable.', (SELECT ItemName FROM MENU_ITEM WHERE MenuItemID = NEW.MenuItemID), NEW.MenuItemID;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_set_unitprice_on_insert
    BEFORE INSERT ON ORDER_DETAIL
    FOR EACH ROW
    EXECUTE FUNCTION trg_set_order_detail_unitprice();


-- 5. FUNCTION & TRIGGER: Tính toán lại tổng tiền và giảm giá đơn hàng (trg_recalculate_order_amount)
CREATE OR REPLACE FUNCTION trg_recalculate_order_amount()
RETURNS TRIGGER AS $$
DECLARE
current_order_id INT;
    order_total NUMERIC(10,2) := 0;
    rank_discount_calc NUMERIC(10,2) := 0;
    promo_discount_calc NUMERIC(10,2) := 0;
    customer_rec CUSTOMER%ROWTYPE;
    membership_rank_rec MEMBERSHIP_RANK%ROWTYPE;
    promotion_rec EVENT_PROMOTION%ROWTYPE;
    total_min_selling_price NUMERIC(10,2) := 0;
    final_order_amount_vnd NUMERIC(10,2);
    current_exchange_rate NUMERIC(10,4);
BEGIN
    IF TG_OP = 'DELETE' THEN
        current_order_id := OLD.OrderID;
ELSE
        current_order_id := NEW.OrderID;
END IF;

SELECT ExchangeRate INTO current_exchange_rate
FROM CUSTOMER_ORDER
WHERE OrderID = current_order_id;

SELECT COALESCE(SUM(od.Quantity * od.UnitPrice), 0)
INTO order_total
FROM ORDER_DETAIL od
WHERE od.OrderID = current_order_id;

SELECT CustomerID, PromotionID
INTO customer_rec.CustomerID, promotion_rec.PromotionID
FROM CUSTOMER_ORDER
WHERE OrderID = current_order_id;

IF customer_rec.CustomerID IS NOT NULL THEN
SELECT TotalSpent, Rank INTO customer_rec.TotalSpent, customer_rec.Rank
FROM CUSTOMER
WHERE CustomerID = customer_rec.CustomerID;

SELECT * INTO membership_rank_rec
FROM MEMBERSHIP_RANK
WHERE customer_rec.Rank = RankName;

IF FOUND AND membership_rank_rec.DiscountRate IS NOT NULL THEN
            rank_discount_calc := order_total * membership_rank_rec.DiscountRate;
END IF;
END IF;

    IF promotion_rec.PromotionID IS NOT NULL THEN
SELECT * INTO promotion_rec FROM EVENT_PROMOTION WHERE PromotionID = promotion_rec.PromotionID;
IF FOUND THEN
            IF promotion_rec.MinOrderAmount IS NOT NULL AND order_total < promotion_rec.MinOrderAmount THEN
                promo_discount_calc := 0;
ELSE
                IF promotion_rec.PromotionType = 'Percent' THEN
                    promo_discount_calc := order_total * promotion_rec.Value / 100;
                ELSIF promotion_rec.PromotionType = 'Fixed' THEN
                    promo_discount_calc := promotion_rec.Value;
END IF;
END IF;
END IF;
END IF;

SELECT COALESCE(SUM(mi.MinSellingPrice * od.Quantity), 0)
INTO total_min_selling_price
FROM ORDER_DETAIL od
         JOIN MENU_ITEM mi ON od.MenuItemID = mi.MenuItemID
WHERE od.OrderID = current_order_id;

DECLARE
current_total_after_discounts NUMERIC(10,2);
        max_allowed_discount NUMERIC(10,2);
BEGIN
        current_total_after_discounts := order_total - rank_discount_calc - promo_discount_calc;

        IF current_total_after_discounts < total_min_selling_price THEN
            max_allowed_discount := order_total - total_min_selling_price;

            IF (rank_discount_calc + promo_discount_calc) > max_allowed_discount THEN
                IF rank_discount_calc > max_allowed_discount THEN
                    rank_discount_calc := max_allowed_discount;
                    promo_discount_calc := 0;
ELSE
                    promo_discount_calc := GREATEST(0, max_allowed_discount - rank_discount_calc);
END IF;
END IF;
END IF;
END;

    final_order_amount_vnd := order_total - rank_discount_calc - promo_discount_calc;

UPDATE CUSTOMER_ORDER
SET TotalAmount = final_order_amount_vnd,
    TotalAmountUSD = ROUND(final_order_amount_vnd / current_exchange_rate, 2),
    RankDiscount = rank_discount_calc,
    PromotionDiscount = promo_discount_calc
WHERE OrderID = current_order_id;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_recalc_after_insert
    AFTER INSERT ON ORDER_DETAIL
    FOR EACH ROW
    EXECUTE FUNCTION trg_recalculate_order_amount();

CREATE TRIGGER trg_recalc_after_update
    AFTER UPDATE ON ORDER_DETAIL
    FOR EACH ROW
    EXECUTE FUNCTION trg_recalculate_order_amount();

CREATE TRIGGER trg_recalc_after_delete
    AFTER DELETE ON ORDER_DETAIL
    FOR EACH ROW
    EXECUTE FUNCTION trg_recalculate_order_amount();


-- 6. FUNCTION & TRIGGER: Cập nhật tổng chi tiêu của khách hàng khi đơn hàng hoàn thành (trg_update_customer_total_spent)
CREATE OR REPLACE FUNCTION trg_update_customer_total_spent()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.OrderStatus <> 'Completed' AND NEW.OrderStatus = 'Completed' AND NEW.CustomerID IS NOT NULL THEN
UPDATE CUSTOMER
SET TotalSpent = TotalSpent + NEW.TotalAmount
WHERE CustomerID = NEW.CustomerID;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_customer_total_spent
    AFTER UPDATE ON CUSTOMER_ORDER
    FOR EACH ROW
    WHEN (OLD.OrderStatus IS DISTINCT FROM NEW.OrderStatus AND NEW.OrderStatus = 'Completed')
EXECUTE FUNCTION trg_update_customer_total_spent();


-- 7. FUNCTION & TRIGGER: Tự động trừ số lượng cốc/ly khi đơn hàng hoàn thành (trg_decrease_inventory_on_completion)
CREATE OR REPLACE FUNCTION trg_decrease_inventory_on_completion()
RETURNS TRIGGER AS $$
DECLARE
total_items_in_order INT := 0;
    cup_ingredient_id INT;
BEGIN
SELECT IngredientID INTO cup_ingredient_id
FROM INGREDIENT
WHERE IngredientName = 'Cốc/Ly Tiêu Chuẩn';

IF NOT FOUND THEN
        RAISE EXCEPTION 'Nguyên liệu "Cốc/Ly Tiêu Chuẩn" không tìm thấy trong kho. Vui lòng thêm vào bảng INGREDIENT.';
END IF;

    IF OLD.OrderStatus <> 'Completed' AND NEW.OrderStatus = 'Completed' THEN
SELECT COALESCE(SUM(Quantity), 0)
INTO total_items_in_order
FROM ORDER_DETAIL
WHERE OrderID = NEW.OrderID;

IF total_items_in_order > 0 THEN
UPDATE INGREDIENT
SET CurrentStock = CurrentStock - total_items_in_order
WHERE IngredientID = cup_ingredient_id;
END IF;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_decrease_inventory ON CUSTOMER_ORDER;
CREATE TRIGGER trg_decrease_inventory
    AFTER UPDATE ON CUSTOMER_ORDER
    FOR EACH ROW
    WHEN (OLD.OrderStatus IS DISTINCT FROM NEW.OrderStatus AND NEW.OrderStatus = 'Completed')
EXECUTE FUNCTION trg_decrease_inventory_on_completion();


-- 8. FUNCTION & TRIGGER: Cập nhật tồn kho khi đơn hàng nhập kho được giao (trg_update_inventory_po_delivery)
CREATE OR REPLACE FUNCTION trg_update_inventory_on_purchase_delivery()
RETURNS TRIGGER AS $$
DECLARE
po_detail RECORD;
BEGIN
    IF OLD.OrderStatus <> 'Delivered' AND NEW.OrderStatus = 'Delivered' THEN
        FOR po_detail IN SELECT IngredientID, QuantityOrdered FROM PURCHASE_ORDER_DETAIL WHERE PurchaseOrderID = NEW.PurchaseOrderID LOOP
UPDATE INGREDIENT
SET CurrentStock = CurrentStock + po_detail.QuantityOrdered,
    LastRestockDate = CURRENT_DATE
WHERE IngredientID = po_detail.IngredientID;
END LOOP;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_inventory_po_delivery
    AFTER UPDATE ON PURCHASE_ORDER
    FOR EACH ROW
    EXECUTE FUNCTION trg_update_inventory_on_purchase_delivery();


-- 9. FUNCTION & TRIGGER: Xử lý khi đơn hàng bị hủy (trg_handle_order_cancellation)
CREATE OR REPLACE FUNCTION trg_handle_order_cancellation()
RETURNS TRIGGER AS $$
DECLARE
total_items_in_order INT := 0;
    cup_ingredient_id INT;
BEGIN
SELECT IngredientID INTO cup_ingredient_id
FROM INGREDIENT
WHERE IngredientName = 'Cốc/Ly Tiêu Chuẩn';

IF NOT FOUND THEN
        RAISE EXCEPTION 'Nguyên liệu "Cốc/Ly Tiêu Chuẩn" không tìm thấy trong kho. Vui lòng thêm vào bảng INGREDIENT.';
END IF;

    IF OLD.OrderStatus <> 'Canceled' AND NEW.OrderStatus = 'Canceled' THEN
        IF OLD.PromotionID IS NOT NULL AND OLD.OrderStatus = 'Completed' THEN
UPDATE EVENT_PROMOTION
SET RemainingUses = RemainingUses + 1
WHERE PromotionID = OLD.PromotionID;
END IF;

        IF OLD.OrderStatus = 'Completed' AND OLD.CustomerID IS NOT NULL THEN
UPDATE CUSTOMER
SET TotalSpent = TotalSpent - OLD.TotalAmount
WHERE CustomerID = OLD.CustomerID;
END IF;

        IF OLD.OrderStatus IN ('Completed', 'Ready for Pickup') THEN
SELECT COALESCE(SUM(Quantity), 0)
INTO total_items_in_order
FROM ORDER_DETAIL
WHERE OrderID = OLD.OrderID;

IF total_items_in_order > 0 THEN
UPDATE INGREDIENT
SET CurrentStock = CurrentStock + total_items_in_order
WHERE IngredientID = cup_ingredient_id;
END IF;
END IF;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_handle_order_cancellation ON CUSTOMER_ORDER;
CREATE TRIGGER trg_handle_order_cancellation
    AFTER UPDATE ON CUSTOMER_ORDER
    FOR EACH ROW
    WHEN (OLD.OrderStatus IS DISTINCT FROM NEW.OrderStatus AND NEW.OrderStatus = 'Canceled')
EXECUTE FUNCTION trg_handle_order_cancellation();

---
--- CÁC HÀM VÀ TRIGGER PHÂN QUYỀN (KHÔNG CÓ MẬT KHẨU)
---

-- 10. Hàm Trigger Chung để Đọc Biến Session và Xác thực vai trò
CREATE OR REPLACE FUNCTION trg_require_role_for_action()
RETURNS TRIGGER AS $$
DECLARE
v_role_name VARCHAR(50);
    action_description TEXT := 'thực hiện thao tác';
    required_role VARCHAR(50);
BEGIN
    -- Lấy RoleName từ biến session
    v_role_name := current_setting('app.auth_role_name', TRUE);

    -- Xác định vai trò cần thiết cho hành động này dựa trên bảng và thao tác
    IF TG_TABLE_NAME = 'customer_order' THEN
        IF TG_OP = 'UPDATE' THEN
            required_role := 'Manager';
            action_description := 'chỉnh sửa đơn hàng (OrderID: ' || OLD.OrderID || ')';
        ELSIF TG_OP = 'DELETE' THEN
            required_role := 'Manager';
            action_description := 'xóa đơn hàng (OrderID: ' || OLD.OrderID || ')';
END IF;
    ELSIF TG_TABLE_NAME = 'menu_item' THEN
        IF TG_OP = 'INSERT' THEN
            required_role := 'Manager';
            action_description := 'thêm món vào thực đơn';
        ELSIF TG_OP = 'UPDATE' THEN
            required_role := 'Manager';
            action_description := 'cập nhật món trong thực đơn (MenuItemID: ' || OLD.MenuItemID || ')';
        ELSIF TG_OP = 'DELETE' THEN
            required_role := 'Manager';
            action_description := 'xóa món khỏi thực đơn (MenuItemID: ' || OLD.MenuItemID || ')';
END IF;
    ELSIF TG_TABLE_NAME = 'employee' THEN
        IF TG_OP = 'INSERT' THEN
            required_role := 'Manager';
            action_description := 'thêm nhân viên mới';
        ELSIF TG_OP = 'UPDATE' THEN
            required_role := 'Manager';
            action_description := 'cập nhật thông tin nhân viên (EmployeeID: ' || OLD.EmployeeID || ')';
        ELSIF TG_OP = 'DELETE' THEN
            required_role := 'Manager';
            action_description := 'xóa nhân viên (EmployeeID: ' || OLD.EmployeeID || ')';
END IF;
    ELSIF TG_TABLE_NAME = 'event_promotion' THEN
        IF TG_OP = 'INSERT' THEN
            required_role := 'Manager';
            action_description := 'thêm khuyến mãi mới';
        ELSIF TG_OP = 'UPDATE' THEN
            required_role := 'Manager';
            action_description := 'cập nhật khuyến mãi (PromotionID: ' || OLD.PromotionID || ')';
        ELSIF TG_OP = 'DELETE' THEN
            required_role := 'Manager';
            action_description := 'xóa khuyến mãi (PromotionID: ' || OLD.PromotionID || ')';
END IF;
    ELSIF TG_TABLE_NAME = 'purchase_order' THEN
        IF TG_OP = 'INSERT' THEN
            required_role := 'Manager';
            action_description := 'tạo đơn nhập hàng mới';
        ELSIF TG_OP = 'UPDATE' THEN
            required_role := 'Manager';
            action_description := 'cập nhật đơn nhập hàng (PurchaseOrderID: ' || OLD.PurchaseOrderID || ')';
        ELSIF TG_OP = 'DELETE' THEN
            required_role := 'Manager';
            action_description := 'xóa đơn nhập hàng (PurchaseOrderID: ' || OLD.PurchaseOrderID || ')';
END IF;
    ELSIF TG_TABLE_NAME = 'ingredient' THEN
        IF TG_OP = 'INSERT' THEN
            required_role := 'Manager';
            action_description := 'thêm nguyên liệu mới';
        ELSIF TG_OP = 'UPDATE' THEN
            required_role := 'Manager';
            action_description := 'cập nhật thông tin nguyên liệu (IngredientID: ' || OLD.IngredientID || ')';
        ELSIF TG_OP = 'DELETE' THEN
            required_role := 'Manager';
            action_description := 'xóa nguyên liệu (IngredientID: ' || OLD.IngredientID || ')';
END IF;
ELSE
        -- Mặc định cho phép các thao tác khác nếu không có yêu cầu vai trò cụ thể
        RETURN NEW;
END IF;

    -- Kiểm tra xem biến session có được đặt không
    IF v_role_name IS NULL THEN
        RAISE EXCEPTION 'Vai trò phải được cung cấp qua biến session (app.auth_role_name) để %.', action_description;
END IF;

    -- Kiểm tra xem vai trò được cung cấp có đúng là vai trò cần thiết không
    IF v_role_name IS DISTINCT FROM required_role THEN
        RAISE EXCEPTION 'Vai trò "%" không được phép %.', v_role_name, action_description;
END IF;

RETURN NEW; -- Cho phép thao tác nếu xác thực thành công
END;
$$ LANGUAGE plpgsql;


---
--- Áp dụng Triggers cho các Thao tác cụ thể
---

-- Trigger cho thao tác DELETE trên CUSTOMER_ORDER
DROP TRIGGER IF EXISTS trg_check_role_on_order_delete ON CUSTOMER_ORDER;
CREATE TRIGGER trg_check_role_on_order_delete
    BEFORE DELETE ON CUSTOMER_ORDER
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác UPDATE trên CUSTOMER_ORDER (chỉ khi thay đổi các trường quan trọng)
DROP TRIGGER IF EXISTS trg_check_role_on_order_update ON CUSTOMER_ORDER;
CREATE TRIGGER trg_check_role_on_order_update
    BEFORE UPDATE ON CUSTOMER_ORDER
    FOR EACH ROW
    WHEN (
    OLD.OrderStatus IS DISTINCT FROM NEW.OrderStatus
    OR OLD.TotalAmount IS DISTINCT FROM NEW.TotalAmount
    OR OLD.PromotionID IS DISTINCT FROM NEW.PromotionID
    OR OLD.IsPaid IS DISTINCT FROM NEW.IsPaid
)
EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác INSERT trên MENU_ITEM
DROP TRIGGER IF EXISTS trg_check_role_on_menu_item_insert ON MENU_ITEM;
CREATE TRIGGER trg_check_role_on_menu_item_insert
    BEFORE INSERT ON MENU_ITEM
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác UPDATE trên MENU_ITEM
DROP TRIGGER IF EXISTS trg_check_role_on_menu_item_update ON MENU_ITEM;
CREATE TRIGGER trg_check_role_on_menu_item_update
    BEFORE UPDATE ON MENU_ITEM
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác DELETE trên MENU_ITEM
DROP TRIGGER IF EXISTS trg_check_role_on_menu_item_delete ON MENU_ITEM;
CREATE TRIGGER trg_check_role_on_menu_item_delete
    BEFORE DELETE ON MENU_ITEM
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác INSERT trên EMPLOYEE
DROP TRIGGER IF EXISTS trg_check_role_on_employee_insert ON EMPLOYEE;
CREATE TRIGGER trg_check_role_on_employee_insert
    BEFORE INSERT ON EMPLOYEE
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác UPDATE trên EMPLOYEE
DROP TRIGGER IF EXISTS trg_check_role_on_employee_update ON EMPLOYEE;
CREATE TRIGGER trg_check_role_on_employee_update
    BEFORE UPDATE ON EMPLOYEE
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác DELETE trên EMPLOYEE
DROP TRIGGER IF EXISTS trg_check_role_on_employee_delete ON EMPLOYEE;
CREATE TRIGGER trg_check_role_on_employee_delete
    BEFORE DELETE ON EMPLOYEE
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác INSERT trên EVENT_PROMOTION
DROP TRIGGER IF EXISTS trg_check_role_on_promo_insert ON EVENT_PROMOTION;
CREATE TRIGGER trg_check_role_on_promo_insert
    BEFORE INSERT ON EVENT_PROMOTION
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác UPDATE trên EVENT_PROMOTION
DROP TRIGGER IF EXISTS trg_check_role_on_promo_update ON EVENT_PROMOTION;
CREATE TRIGGER trg_check_role_on_promo_update
    BEFORE UPDATE ON EVENT_PROMOTION
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác DELETE trên EVENT_PROMOTION
DROP TRIGGER IF EXISTS trg_check_role_on_promo_delete ON EVENT_PROMOTION;
CREATE TRIGGER trg_check_role_on_promo_delete
    BEFORE DELETE ON EVENT_PROMOTION
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác INSERT trên PURCHASE_ORDER
DROP TRIGGER IF EXISTS trg_check_role_on_po_insert ON PURCHASE_ORDER;
CREATE TRIGGER trg_check_role_on_po_insert
    BEFORE INSERT ON PURCHASE_ORDER
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác UPDATE trên PURCHASE_ORDER
DROP TRIGGER IF EXISTS trg_check_role_on_po_update ON PURCHASE_ORDER;
CREATE TRIGGER trg_check_role_on_po_update
    BEFORE UPDATE ON PURCHASE_ORDER
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác DELETE trên PURCHASE_ORDER
DROP TRIGGER IF EXISTS trg_check_role_on_po_delete ON PURCHASE_ORDER;
CREATE TRIGGER trg_check_role_on_po_delete
    BEFORE DELETE ON PURCHASE_ORDER
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác INSERT trên INGREDIENT
DROP TRIGGER IF EXISTS trg_check_role_on_ingredient_insert ON INGREDIENT;
CREATE TRIGGER trg_check_role_on_ingredient_insert
    BEFORE INSERT ON INGREDIENT
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác UPDATE trên INGREDIENT
DROP TRIGGER IF EXISTS trg_check_role_on_ingredient_update ON INGREDIENT;
CREATE TRIGGER trg_check_role_on_ingredient_update
    BEFORE UPDATE ON INGREDIENT
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- Trigger cho thao tác DELETE trên INGREDIENT
DROP TRIGGER IF EXISTS trg_check_role_on_ingredient_delete ON INGREDIENT;
CREATE TRIGGER trg_check_role_on_ingredient_delete
    BEFORE DELETE ON INGREDIENT
    FOR EACH ROW
    EXECUTE FUNCTION trg_require_role_for_action();

-- =============================================
-- CÁC VIEW (HIỂN THỊ TÊN THAY VÌ ID VÀ CẢ USD)
-- =============================================

-- 1. Daily Sales Summary View (Báo cáo doanh số hàng ngày)
CREATE OR REPLACE VIEW DailySalesSummary AS
SELECT
    DATE_TRUNC('day', OrderTime) AS SaleDate,
    COUNT(OrderID) AS NumberOfOrders,
    SUM(TotalAmount) AS TotalRevenueVND,
    SUM(TotalAmountUSD) AS TotalRevenueUSD,
    SUM(RankDiscount) AS TotalRankDiscountGivenVND,
    SUM(PromotionDiscount) AS TotalPromotionDiscountGivenVND
FROM
    CUSTOMER_ORDER
WHERE
    OrderStatus = 'Completed'
GROUP BY
    DATE_TRUNC('day', OrderTime)
ORDER BY
    SaleDate DESC;

-- 2. Low Stock Ingredients View (Nguyên liệu tồn kho thấp)
CREATE OR REPLACE VIEW LowStockIngredients AS
SELECT
    IngredientID,
    IngredientName,
    UnitOfMeasure,
    CurrentStock,
    MinStockLevel,
    (CurrentStock - MinStockLevel) AS StockDifference
FROM
    INGREDIENT
WHERE
    CurrentStock <= MinStockLevel
ORDER BY
    StockDifference ASC;

-- 3. Customer Order Summary View (Tổng quan đơn hàng của khách hàng)
CREATE OR REPLACE VIEW CustomerOrderSummary AS
SELECT
    co.OrderID,
    co.OrderTime,
    c.CustomerName,
    c.PhoneNumber AS CustomerPhone,
    e.EmployeeName AS HandledByEmployee,
    ep.PromotionName,
    ep.Description AS PromotionDescription,
    co.TotalAmount AS TotalAmountVND,
    co.TotalAmountUSD,
    co.ExchangeRate,
    co.RankDiscount,
    co.PromotionDiscount,
    co.OrderStatus,
    co.PaymentMethod,
    co.Notes
FROM
    CUSTOMER_ORDER co
        LEFT JOIN
    CUSTOMER c ON co.CustomerID = c.CustomerID
        LEFT JOIN
    EMPLOYEE e ON co.EmployeeID = e.EmployeeID
        LEFT JOIN
    EVENT_PROMOTION ep ON co.PromotionID = ep.PromotionID
ORDER BY
    co.OrderTime DESC;

-- 4. Product Sales Performance View (Hiệu suất bán hàng của sản phẩm)
CREATE OR REPLACE VIEW ProductSalesPerformance AS
SELECT
    mi.MenuItemID,
    mi.ItemName,
    mi.Category,
    SUM(od.Quantity) AS TotalQuantitySold,
    SUM(od.Quantity * od.UnitPrice) AS GrossRevenueVND,
    SUM(od.Quantity * od.UnitPrice / co.ExchangeRate) AS GrossRevenueUSD,
    COUNT(DISTINCT co.OrderID) AS NumberOfOrdersContainingItem
FROM
    ORDER_DETAIL od
        JOIN
    MENU_ITEM mi ON od.MenuItemID = mi.MenuItemID
        JOIN
    CUSTOMER_ORDER co ON od.OrderID = co.OrderID
WHERE
    co.OrderStatus = 'Completed'
GROUP BY
    mi.MenuItemID, mi.ItemName, mi.Category
ORDER BY
    TotalQuantitySold DESC;

-- 5. Customer Rank Info View (Thông tin hạng thành viên khách hàng)
CREATE OR REPLACE VIEW CustomerRankInfo AS
SELECT
    c.CustomerID,
    c.CustomerName,
    c.PhoneNumber,
    c.TotalSpent AS TotalSpentVND,
    (c.TotalSpent / COALESCE((SELECT ExchangeRate FROM CUSTOMER_ORDER ORDER BY OrderTime DESC LIMIT 1), 25000)) AS TotalSpentUSD,
    c.Rank AS CurrentRank,
    mr.DiscountRate AS RankDiscountRate
FROM
    CUSTOMER c
        JOIN
    MEMBERSHIP_RANK mr ON c.Rank = mr.RankName
ORDER BY
    c.TotalSpent DESC;
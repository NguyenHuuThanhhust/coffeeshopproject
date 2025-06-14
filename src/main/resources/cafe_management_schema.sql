-- =========================================================================================
-- COFFEE SHOP MANAGEMENT DATABASE 
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
DROP TABLE IF EXISTS ROLES CASCADE;
-- END: DROP TABLES

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

-- =============================================
-- 2. CUSTOMER TABLE (Khách hàng)
-- =============================================
CREATE TABLE CUSTOMER (
                          CustomerID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                          CustomerName VARCHAR(100) NOT NULL,
                          PhoneNumber VARCHAR(20) UNIQUE NOT NULL,
                          Email VARCHAR(100) UNIQUE,
                          DateJoined DATE DEFAULT CURRENT_DATE,
                          TotalSpent NUMERIC(15, 2) DEFAULT 0,
                          Rank VARCHAR(50) DEFAULT 'Bronze',
                          FOREIGN KEY (Rank) REFERENCES MEMBERSHIP_RANK(RankName)
);

CREATE INDEX idx_customer_rank ON CUSTOMER (Rank);


-- =============================================
-- 3. EMPLOYEE TABLE (Nhân viên)
-- =============================================
CREATE TABLE EMPLOYEE (
                          EmployeeID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, 
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
                           MenuItemID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
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
                                 PromotionID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
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
                                OrderID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                CustomerID BIGINT REFERENCES CUSTOMER(CustomerID),
                                EmployeeID BIGINT REFERENCES EMPLOYEE(EmployeeID),
                                PromotionID BIGINT REFERENCES EVENT_PROMOTION(PromotionID),
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

-- Indexes cho các cột FK và cột OrderTime thường được dùng trong truy vấn
CREATE INDEX idx_customer_order_customerid ON CUSTOMER_ORDER (CustomerID);
CREATE INDEX idx_customer_order_employeeid ON CUSTOMER_ORDER (EmployeeID);
CREATE INDEX idx_customer_order_promotionid ON CUSTOMER_ORDER (PromotionID);
CREATE INDEX idx_customer_order_ordertime ON CUSTOMER_ORDER (OrderTime DESC); -- Tối ưu cho ORDER BY OrderTime DESC trong view
CREATE INDEX idx_customer_order_orderstatus ON CUSTOMER_ORDER (OrderStatus); -- Có thể hữu ích cho view DailySalesSummary

-- =============================================
-- 7. ORDER_DETAIL TABLE (Chi tiết đơn hàng)
-- =============================================
CREATE TABLE ORDER_DETAIL (
                              OrderDetailID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                              OrderID BIGINT NOT NULL REFERENCES CUSTOMER_ORDER(OrderID) ON DELETE CASCADE,
                              MenuItemID BIGINT NOT NULL REFERENCES MENU_ITEM(MenuItemID),
                              Quantity INT NOT NULL,
                              UnitPrice NUMERIC(10, 2) NOT NULL
);

-- Indexes cho các cột FK
CREATE INDEX idx_order_detail_orderid ON ORDER_DETAIL (OrderID);
CREATE INDEX idx_order_detail_menuitemid ON ORDER_DETAIL (MenuItemID);

-- =============================================
-- CÁC BẢNG QUẢN LÝ TỒN KHO NGUYÊN LIỆU
-- =============================================

-- 8. SUPPLIER TABLE (Nhà cung cấp)
CREATE TABLE SUPPLIER (
                          SupplierID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                          SupplierName VARCHAR(100) NOT NULL UNIQUE,
                          ContactPerson VARCHAR(100),
                          PhoneNumber VARCHAR(20),
                          Email VARCHAR(100),
                          Address TEXT
);

-- 9. INGREDIENT TABLE (Nguyên liệu / Tồn kho)
CREATE TABLE INGREDIENT (
                            IngredientID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                            IngredientName VARCHAR(100) NOT NULL UNIQUE,
                            UnitOfMeasure VARCHAR(20) NOT NULL,
                            CurrentStock NUMERIC(10,2) DEFAULT 0 NOT NULL,
                            MinStockLevel NUMERIC(10,2) DEFAULT 0,
                            LastRestockDate DATE
);
CREATE INDEX idx_ingredient_name ON INGREDIENT (IngredientName);
--Thêm Cốc/ly tiêu chuẩn vào kho ( Kiểm soát số lượng cốc hiện có)
SET app.auth_role_name = 'Manager';
INSERT INTO INGREDIENT (IngredientID, IngredientName, UnitOfMeasure, CurrentStock, MinStockLevel, LastRestockDate)
    OVERRIDING SYSTEM VALUE
VALUES (5, 'Cốc/Ly Tiêu Chuẩn', 'cái', 100.0, 100.0, '2025-06-01')
ON CONFLICT (IngredientID) DO UPDATE SET
                                         IngredientName = EXCLUDED.IngredientName, UnitOfMeasure = EXCLUDED.UnitOfMeasure,
                                         CurrentStock = EXCLUDED.CurrentStock, MinStockLevel = EXCLUDED.MinStockLevel,
                                         LastRestockDate = EXCLUDED.LastRestockDate;
RESET app.auth_role_name;
-- =============================================
-- 10. PURCHASE_ORDER TABLE (Đơn đặt hàng nhập kho)
-- =============================================
CREATE TABLE PURCHASE_ORDER (
                                PurchaseOrderID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                SupplierID BIGINT NOT NULL REFERENCES SUPPLIER(SupplierID), -- SỬA: SupplierID là BIGINT
                                OrderDate DATE DEFAULT CURRENT_DATE,
                                ExpectedDeliveryDate DATE,
                                ActualDeliveryDate DATE,
                                TotalAmount NUMERIC(10,2) DEFAULT 0,
                                OrderStatus VARCHAR(30) DEFAULT 'Pending'
);

-- Indexes cho cột FK và OrderDate
CREATE INDEX idx_purchase_order_supplierid ON PURCHASE_ORDER (SupplierID);
CREATE INDEX idx_purchase_order_orderdate ON PURCHASE_ORDER (OrderDate DESC);


-- =============================================
-- 11. PURCHASE_ORDER_DETAIL TABLE (Chi tiết đơn đặt hàng nhập kho)
-- =============================================
CREATE TABLE PURCHASE_ORDER_DETAIL (
                                       PurchaseOrderDetailID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                       PurchaseOrderID BIGINT NOT NULL REFERENCES PURCHASE_ORDER(PurchaseOrderID) ON DELETE CASCADE,
                                       IngredientID BIGINT NOT NULL REFERENCES INGREDIENT(IngredientID),
                                       QuantityOrdered NUMERIC(10,2) NOT NULL,
                                       UnitPrice NUMERIC(10,2) NOT NULL,
                                       UNIQUE (PurchaseOrderID, IngredientID)
);

-- Indexes cho các cột FK
CREATE INDEX idx_purchase_order_detail_purchaseorderid ON PURCHASE_ORDER_DETAIL (PurchaseOrderID);
CREATE INDEX idx_purchase_order_detail_ingredientid ON PURCHASE_ORDER_DETAIL (IngredientID);

-- =============================================
-- 12. ROLES TABLE (Bảng Vai trò)
-- =============================================
CREATE TABLE ROLES (
                       RoleName VARCHAR(50) PRIMARY KEY
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
    current_order_id BIGINT;
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

-- 7. FUNCTION: Cập nhật tồn kho khi đơn hàng nhập kho hoàn thành
CREATE OR REPLACE FUNCTION update_ingredient_stock_on_po_completion()
    RETURNS TRIGGER AS $$
DECLARE
    v_ingredient_id BIGINT;
    v_quantity_ordered NUMERIC(10, 2);
BEGIN
    IF OLD.orderstatus IS DISTINCT FROM 'Completed' AND NEW.orderstatus = 'Completed' THEN
        FOR v_ingredient_id, v_quantity_ordered IN
            SELECT ingredientid, quantityordered
            FROM purchase_order_detail
            WHERE purchaseorderid = NEW.purchaseorderid
            LOOP
                UPDATE ingredient
                SET
                    currentstock = currentstock + v_quantity_ordered,
                    lastrestockdate = CURRENT_DATE
                WHERE
                    ingredientid = v_ingredient_id;
            END LOOP;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 8. TRIGGER: Trigger để gọi hàm khi có sự kiện
DROP TRIGGER IF EXISTS trg_update_inventory_po_delivery ON PURCHASE_ORDER;
CREATE TRIGGER after_purchase_order_update_status
    AFTER UPDATE ON purchase_order
    FOR EACH ROW
EXECUTE FUNCTION update_ingredient_stock_on_po_completion();

DROP TRIGGER IF EXISTS trg_decrease_cups_on_insert ON CUSTOMER_ORDER;
DROP FUNCTION IF EXISTS decrease_cups_on_order_insert() CASCADE;
DROP TRIGGER IF EXISTS trg_handle_order_cancellation ON CUSTOMER_ORDER;
DROP FUNCTION IF EXISTS handle_order_cancellation() CASCADE;


-- 9. NEW FUNCTION: logic giảm/tăng tồn kho cốc khi đơn hàng được thêm /hủy
CREATE OR REPLACE FUNCTION decrease_cups_on_order_insert()
    RETURNS TRIGGER AS $$
DECLARE
    cup_ingredient_id BIGINT;
BEGIN
    SELECT IngredientID INTO cup_ingredient_id
    FROM INGREDIENT
    WHERE IngredientName = 'Cốc/Ly Tiêu Chuẩn';

    IF NOT FOUND THEN
        RAISE WARNING 'Không tìm thấy nguyên liệu "Cốc/Ly Tiêu Chuẩn". Không thể trừ tồn kho.';
        RETURN NEW;
    END IF;

    -- Trừ số lượng cốc tương ứng với số lượng món trong đơn
    UPDATE INGREDIENT
    SET CurrentStock = CurrentStock - NEW.Quantity
    WHERE IngredientID = cup_ingredient_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION handle_order_cancellation()
    RETURNS TRIGGER AS $$
DECLARE
    total_items INT := 0;
    cup_ingredient_id BIGINT;
BEGIN
    -- Lấy ID của nguyên liệu "Cốc/Ly Tiêu Chuẩn"
    SELECT IngredientID INTO cup_ingredient_id
    FROM INGREDIENT
    WHERE IngredientName = 'Cốc/Ly Tiêu Chuẩn';

    IF NOT FOUND THEN
        RAISE WARNING 'Không tìm thấy nguyên liệu "Cốc/Ly Tiêu Chuẩn" trong bảng INGREDIENT.';
        RETURN NEW;
    END IF;

    -- Nếu đơn bị hủy từ trạng thái khác
    IF OLD.OrderStatus IS DISTINCT FROM 'Canceled' AND NEW.OrderStatus = 'Canceled' THEN

        -- Nếu đơn trước đó là Completed thì cộng lại khuyến mãi
        IF OLD.PromotionID IS NOT NULL AND OLD.OrderStatus = 'Completed' THEN
            UPDATE EVENT_PROMOTION
            SET RemainingUses = RemainingUses + 1
            WHERE PromotionID = OLD.PromotionID;
        END IF;

        -- Nếu đơn đã hoàn thành và có khách hàng, trừ lại tổng chi tiêu
        IF OLD.OrderStatus = 'Completed' AND OLD.CustomerID IS NOT NULL THEN
            UPDATE CUSTOMER
            SET TotalSpent = TotalSpent - OLD.TotalAmount
            WHERE CustomerID = OLD.CustomerID;
        END IF;

        -- Cộng lại số cốc đã dùng
        SELECT COALESCE(SUM(Quantity), 0)
        INTO total_items
        FROM ORDER_DETAIL
        WHERE OrderID = OLD.OrderID;

        IF total_items > 0 THEN
            UPDATE INGREDIENT
            SET CurrentStock = CurrentStock + total_items
            WHERE IngredientID = cup_ingredient_id;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- 10. NEW TRIGGER: Trigger duy nhất để gọi hàm khi trạng thái đơn hàng thay đổi
CREATE TRIGGER trg_decrease_cups_on_insert
    AFTER INSERT ON ORDER_DETAIL
    FOR EACH ROW
EXECUTE FUNCTION decrease_cups_on_order_insert();

DROP TRIGGER IF EXISTS trg_handle_order_cancellation ON CUSTOMER_ORDER;

CREATE TRIGGER trg_handle_order_cancellation
    AFTER UPDATE ON CUSTOMER_ORDER
    FOR EACH ROW
    WHEN (OLD.OrderStatus IS DISTINCT FROM NEW.OrderStatus AND NEW.OrderStatus = 'Canceled')
EXECUTE FUNCTION handle_order_cancellation();


---
--- CÁC HÀM VÀ TRIGGER PHÂN QUYỀN (KHÔNG CÓ MẬT KHẨU)
---

-- 11. Hàm Trigger Chung để Đọc Biến Session và Xác thực vai trò
CREATE OR REPLACE FUNCTION trg_require_role_for_action()
    RETURNS TRIGGER AS $$
DECLARE
    v_role_name VARCHAR(50);
    action_description TEXT := 'thực hiện thao tác';
    required_role VARCHAR(50);
BEGIN
    v_role_name := current_setting('app.auth_role_name', TRUE);

    IF TG_TABLE_NAME = 'customer_order' THEN
        IF TG_OP = 'UPDATE' THEN
            required_role := 'Manager';
            action_description := 'chỉnh sửa đơn hàng';
        ELSIF TG_OP = 'DELETE' THEN
            required_role := 'Manager';
            action_description := 'xóa đơn hàng';
        END IF;
    ELSIF TG_TABLE_NAME = 'menu_item' THEN
        IF TG_OP = 'INSERT' THEN
            required_role := 'Manager';
            action_description := 'thêm món vào thực đơn';
        ELSIF TG_OP = 'UPDATE' THEN
            required_role := 'Manager';
            action_description := 'cập nhật món trong thực đơn';
        ELSIF TG_OP = 'DELETE' THEN
            required_role := 'Manager';
            action_description := 'xóa món khỏi thực đơn';
        END IF;
    ELSIF TG_TABLE_NAME = 'employee' THEN
        IF TG_OP = 'INSERT' THEN
            required_role := 'Manager';
            action_description := 'thêm nhân viên mới';
        ELSIF TG_OP = 'UPDATE' THEN
            required_role := 'Manager';
            action_description := 'cập nhật thông tin nhân viên';
        ELSIF TG_OP = 'DELETE' THEN
            required_role := 'Manager';
            action_description := 'xóa nhân viên';
        END IF;
    ELSIF TG_TABLE_NAME = 'event_promotion' THEN
        IF TG_OP = 'INSERT' THEN
            required_role := 'Manager';
            action_description := 'thêm khuyến mãi mới';
        ELSIF TG_OP = 'UPDATE' THEN
            required_role := 'Manager';
            action_description := 'cập nhật khuyến mãi';
        ELSIF TG_OP = 'DELETE' THEN
            required_role := 'Manager';
            action_description := 'xóa khuyến mãi';
        END IF;
    ELSIF TG_TABLE_NAME = 'purchase_order' THEN
        IF TG_OP = 'INSERT' THEN
            required_role := 'Manager';
            action_description := 'tạo đơn nhập hàng mới';
        ELSIF TG_OP = 'UPDATE' THEN
            required_role := 'Manager';
            action_description := 'cập nhật đơn nhập hàng';
        ELSIF TG_OP = 'DELETE' THEN
            required_role := 'Manager';
            action_description := 'xóa đơn nhập hàng';
        END IF;
    ELSIF TG_TABLE_NAME = 'ingredient' THEN
        IF TG_OP = 'INSERT' THEN
            required_role := 'Manager';
            action_description := 'thêm nguyên liệu mới';
        ELSIF TG_OP = 'UPDATE' THEN
            required_role := 'Manager';
            action_description := 'cập nhật thông tin nguyên liệu';
        ELSIF TG_OP = 'DELETE' THEN
            required_role := 'Manager';
            action_description := 'xóa nguyên liệu';
        END IF;
    ELSE
        RETURN NEW;
    END IF;

    IF v_role_name IS NULL THEN
        RAISE EXCEPTION 'Vai trò phải được cung cấp qua biến session (app.auth_role_name) để %.', action_description;
    END IF;

    IF v_role_name IS DISTINCT FROM required_role THEN
        RAISE EXCEPTION 'Vai trò "%" không được phép %.', v_role_name, action_description;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


---
--- CÁC VIEW
---

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
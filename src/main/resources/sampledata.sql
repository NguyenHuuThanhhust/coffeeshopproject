SET app.auth_role_name = 'Manager';

-- 1. Dữ liệu cho ROLES (Nếu bảng Roles trống)
INSERT INTO ROLES (RoleName) VALUES
    ('Manager') ON CONFLICT (RoleName) DO NOTHING;
INSERT INTO ROLES (RoleName) VALUES
    ('Staff') ON CONFLICT (RoleName) DO NOTHING;
INSERT INTO ROLES (RoleName) VALUES
    ('Customer') ON CONFLICT (RoleName) DO NOTHING;

-- 2. Dữ liệu cho MEMBERSHIP_RANK
INSERT INTO MEMBERSHIP_RANK (RankName, PointFrom, PointTo, DiscountRate, LoyaltyPointRate)
VALUES
    ('Bronze', 0.00, 999999.99, 0.00, 0.01),
    ('Silver', 1000000.00, 2999999.99, 0.05, 0.02),
    ('Gold', 3000000.00, 6999999.99, 0.10, 0.03),
    ('Platinum', 7000000.00, 9999999999.99, 0.15, 0.05)
    ON CONFLICT (RankName) DO UPDATE SET
    PointFrom = EXCLUDED.PointFrom,
                                  PointTo = EXCLUDED.PointTo,
                                  DiscountRate = EXCLUDED.DiscountRate,
                                  LoyaltyPointRate = EXCLUDED.LoyaltyPointRate;

-- 3. Dữ liệu mẫu cho EMPLOYEE
INSERT INTO EMPLOYEE (EmployeeID, EmployeeName, Position, PhoneNumber, Email, StartDate, HourlyWage)
    OVERRIDING SYSTEM VALUE
VALUES
    (1, 'Lê Thị B', 'Cashier', '0987654321', 'lethib@example.com', '2023-01-15', 25000.00),
    (2, 'Nguyễn Văn C', 'Barista', '0123123123', 'nguyenvanc@example.com', '2022-03-01', 28000.00)
    ON CONFLICT (EmployeeID) DO UPDATE SET
    EmployeeName = EXCLUDED.EmployeeName,
                                    Position = EXCLUDED.Position,
                                    PhoneNumber = EXCLUDED.PhoneNumber,
                                    Email = EXCLUDED.Email,
                                    StartDate = EXCLUDED.StartDate,
                                    HourlyWage = EXCLUDED.HourlyWage;

-- 4. Dữ liệu mẫu cho CUSTOMER
INSERT INTO CUSTOMER (CustomerID, CustomerName, PhoneNumber, Email, DateJoined, TotalSpent, Rank)
    OVERRIDING SYSTEM VALUE
VALUES
    (1, 'Nguyen Hoang A', '0901234567', 'nguyena@example.com', '2024-01-01', 500000.00, 'Bronze'),
    (3, 'Le Hoang F', '0123456789', 'lehoangf@example.com', '2023-05-10', 1500000.00, 'Silver'),
    (4, 'Thanh', '0912789456', 'thanh@example.com', '2024-06-11', 0.00, 'Bronze')
    ON CONFLICT (CustomerID) DO UPDATE SET
    CustomerName = EXCLUDED.CustomerName,
                                    PhoneNumber = EXCLUDED.PhoneNumber,
                                    Email = EXCLUDED.Email,
                                    DateJoined = EXCLUDED.DateJoined,
                                    TotalSpent = EXCLUDED.TotalSpent,
                                    Rank = EXCLUDED.Rank;

-- 5. Dữ liệu mẫu cho MENU_ITEM (Để frontend hiển thị sản phẩm)
INSERT INTO MENU_ITEM (MenuItemID, ItemName, Description, Price, Category, Status, IsAvailable, ImageURL, MinSellingPrice)
    OVERRIDING SYSTEM VALUE
VALUES
    (1, 'Cà phê đen đá', 'Cà phê phin truyền thống', 30000.00, 'Coffee', 'Available', TRUE, 'https://via.placeholder.com/150/0000FF/FFFFFF?text=CafeDen', 25000.00),
    (2, 'Trà Chanh Dây', 'Trà trái cây thơm mát', 35000.00, 'Tea', 'Available', TRUE, 'https://via.placeholder.com/150/FF0000/FFFFFF?text=TraChanhDay', 30000.00),
    (3, 'Trà Sữa Truyền Thống', 'Trà sữa béo ngậy', 30000.00, 'Milk Tea', 'Available', TRUE, 'https://via.placeholder.com/150/00FF00/FFFFFF?text=TraSua', 25000.00),
    (4, 'Cafe Latte', 'Cà phê sữa béo ngậy', 35000.00, 'Coffee', 'Available', TRUE, 'https://via.placeholder.com/150/00FFFF/FFFFFF?text=Latte', 30000.00),
    (5, 'Cafe Cappuccino', 'Cà phê pha bọt sữa', 40000.00, 'Coffee', 'Available', TRUE, 'https://via.placeholder.com/150/FFFF00/FFFFFF?text=Cappuccino', 35000.00),
    (6, 'Trà Đào', 'Trà đào thanh mát', 35000.00, 'Tea', 'Available', TRUE, 'https://via.placeholder.com/150/FF00FF/FFFFFF?text=TraDao', 30000.00),
    (7, 'Soda Việt Quất', 'Soda hương việt quất', 45000.00, 'Soda', 'Available', TRUE, 'https://via.placeholder.com/150/800080/FFFFFF?text=Soda', 40000.00),
    (8, 'Bánh Tiramisu', 'Bánh ngọt Tiramisu', 50000.00, 'Cake', 'Available', TRUE, 'https://via.placeholder.com/150/A0522D/FFFFFF?text=Tiramisu', 45000.00)
    ON CONFLICT (MenuItemID) DO UPDATE SET
    ItemName = EXCLUDED.ItemName,
                                    Description = EXCLUDED.Description,
                                    Price = EXCLUDED.Price,
                                    Category = EXCLUDED.Category,
                                    Status = EXCLUDED.Status,
                                    IsAvailable = EXCLUDED.IsAvailable,
                                    ImageURL = EXCLUDED.ImageURL,
                                    MinSellingPrice = EXCLUDED.MinSellingPrice;


-- 6. Dữ liệu mẫu cho EVENT_PROMOTION
INSERT INTO EVENT_PROMOTION (PromotionID, PromotionName, Description, PromotionType, Value, RemainingUses, StartDate, EndDate, MinOrderAmount)
    OVERRIDING SYSTEM VALUE
VALUES
    (1, 'Mừng Khai Trương 10%', 'Giảm 10% cho mọi đơn hàng', 'Percent', 10.00, 50, '2025-05-01', '2025-06-30', 0.00),
    (2, 'Ưu Đãi Đặc Biệt 30k', 'Giảm giá cố định 30.000đ', 'Fixed', 30000.00, 20, '2025-06-05', '2025-06-20', 100000.00),
    (3, 'FLASH SALE 20%', 'Giảm 20% cho tổng hóa đơn', 'Percent', 20.00, 100, '2025-06-01', '2025-06-30', 50000.00),
    (4, 'Quà tặng kèm', 'Tặng món X khi mua món Y', 'Gift', 0.00, 999, '2025-01-01', '2025-12-31', 0.00)
    ON CONFLICT (PromotionID) DO UPDATE SET
    PromotionName = EXCLUDED.PromotionName,
                                     Description = EXCLUDED.Description,
                                     PromotionType = EXCLUDED.PromotionType,
                                     Value = EXCLUDED.Value,
                                     RemainingUses = EXCLUDED.RemainingUses,
                                     StartDate = EXCLUDED.StartDate,
                                     EndDate = EXCLUDED.EndDate,
                                     MinOrderAmount = EXCLUDED.MinOrderAmount;


-- 7. Dữ liệu mẫu cho INGREDIENT (Để frontend hiển thị tồn kho và test trừ cốc)
INSERT INTO INGREDIENT (IngredientID, IngredientName, UnitOfMeasure, CurrentStock, MinStockLevel, LastRestockDate)
    OVERRIDING SYSTEM VALUE
VALUES
    (1, 'Hạt cà phê Arabica', 'kg', 15.5, 5.0, '2025-06-01'),
    (2, 'Sữa tươi', 'lít', 25.0, 10.0, '2025-06-01'),
    (3, 'Đường', 'kg', 8.0, 2.0, '2025-06-01'),
    (4, 'Bột Matcha', 'kg', 1.2, 1.0, '2025-06-01'),
    (5, 'Cốc/Ly Tiêu Chuẩn', 'cái', 80.0, 100.0, '2025-06-01'), -- DÒNG NÀY ĐỂ THÊM CỐC/LY
    (6, 'Đá viên', 'kg', 50.0, 20.0, '2025-06-01')
    ON CONFLICT (IngredientID) DO UPDATE SET
    IngredientName = EXCLUDED.IngredientName,
                                      UnitOfMeasure = EXCLUDED.UnitOfMeasure,
                                      CurrentStock = EXCLUDED.CurrentStock,
                                      MinStockLevel = EXCLUDED.MinStockLevel,
                                      LastRestockDate = EXCLUDED.LastRestockDate;


RESET app.auth_role_name;

const menuItemsGrid = document.getElementById('menuItemsGrid');
const productMenuItemsGrid = document.getElementById('productMenuItemsGrid');
const cartItemsContainer = document.getElementById('cartItems');
const cartTotalLabel = document.getElementById('cartTotal');
const customerPhoneInput = document.getElementById('customerPhone');
const customerNameInput = document.getElementById('customerName');
const orderNotesInput = document.getElementById('orderNotes');
const searchCustomerBtn = document.getElementById('searchCustomerBtn');
const checkoutBtn = document.getElementById('checkoutBtn');
const categoryButtons = document.querySelectorAll('.category-button');

const menuButtons = document.querySelectorAll('.menu-button');
const homeView = document.getElementById('homeView');
const storeView = document.getElementById('storeView');
const manageProductView = document.getElementById('manageProductView');
const ingredientListContainer = document.getElementById('ingredientList');

let menuItems = [];
let cart = [];
let ingredients = [];

let currentCustomerId = null;

const promotionCodeInput = document.getElementById('promotionCodeInput');
const applyPromotionBtn = document.getElementById('applyPromotionBtn');
const appliedPromotionDisplay = document.getElementById('appliedPromotionDisplay');
let selectedPromotionId = null;
let selectedPromotionName = null;

const addMenuItemModal = document.getElementById('addMenuItemModal');
const addMenuItemBtn = document.getElementById('addMenuItemBtn');
const closeMenuItemModal = addMenuItemModal.querySelector('.close-button');
const addMenuItemForm = document.getElementById('addMenuItemForm');

const addRestockIngredientModal = document.getElementById('addRestockIngredientModal');
const addRestockIngredientBtn = document.getElementById('addRestockIngredientBtn');
const closeIngredientModal = addRestockIngredientModal.querySelector('.close-button');
const addRestockIngredientForm = document.getElementById('addRestockIngredientForm');
const ingredientNameSearch = document.getElementById('ingredientNameSearch');
const ingredientIdFound = document.getElementById('ingredientIdFound');
const ingredientUnit = document.getElementById('ingredientUnit');
const ingredientCurrentStock = document.getElementById('ingredientCurrentStock');
const ingredientMinStockLevel = document.getElementById('ingredientMinStockLevel');
const ingredientQuantityChange = document.getElementById('ingredientQuantityChange');
const searchIngredientBtn = document.getElementById('searchIngredientBtn');
const submitIngredientFormBtn = document.getElementById('submitIngredientForm');

// --- Hàm hiển thị Menu Items (cho Home View) ---
function displayMenuItems(itemsToDisplay) {
    menuItemsGrid.innerHTML = '';
    if (!itemsToDisplay || itemsToDisplay.length === 0) {
        menuItemsGrid.innerHTML = '<p style="text-align: center; color: var(--text-color);">Không có món nào trong thực đơn.</p>';
        return;
    }
    itemsToDisplay.forEach(item => {
        const itemElement = document.createElement('div');
        itemElement.classList.add('menu-item');
        itemElement.dataset.id = item.menuItemId;

        const imageUrl = item.imageUrl && item.imageUrl.trim() !== '' ? item.imageUrl : 'https://via.placeholder.com/150?text=No+Image';
        const itemName = item.itemName || 'Không tên';
        const itemPrice = (item.price || 0).toLocaleString('vi-VN');

        itemElement.innerHTML = `
            <div class="menu-item-details">
                <h3>${itemName}</h3>
                <p>${itemPrice} VND</p>
            </div>
        `;
        itemElement.addEventListener('click', () => {
            addItemToCart(item);
            updateCartDisplay();
        });
        menuItemsGrid.appendChild(itemElement);
    });
}

// --- Hàm hiển thị Menu Items (cho Product View - có thêm nút edit/delete) ---
function displayProductMenuItems(itemsToDisplay) {
    productMenuItemsGrid.innerHTML = '';
    if (!itemsToDisplay || itemsToDisplay.length === 0) {
        productMenuItemsGrid.innerHTML = '<p style="text-align: center; color: var(--text-color);">Không có món nào để quản lý.</p>';
        return;
    }
    itemsToDisplay.forEach(item => {
        const itemElement = document.createElement('div');
        itemElement.classList.add('menu-item');
        itemElement.dataset.id = item.menuItemId;

        itemElement.innerHTML = `
            <div class="menu-item-details">
                <h3>${item.itemName}</h3>
                <p>Giá: ${item.price.toLocaleString('vi-VN')} VND</p>
                <p>Loại: ${item.category}</p>
                <p>Trạng thái: <span>${item.status}</span></p>
</div\>
<div class\="menu\-item\-actions"\>
<button class\="edit\-btn" data\-id\="</span>{item.menuItemId}">Sửa</button>
                <button class="delete-btn" data-id="${item.menuItemId}">Xóa</button>
            </div>
        `;
        productMenuItemsGrid.appendChild(itemElement);

        itemElement.querySelector('.edit-btn').addEventListener('click', (e) => {
            alert('Chức năng sửa món chưa được triển khai.');
        });
        itemElement.querySelector('.delete-btn').addEventListener('click', async (e) => {
            const menuItemId = e.target.dataset.id;
            if (confirm(`Bạn có chắc chắn muốn xóa món "${item.itemName}" (ID: ${menuItemId}) không?`)) {
                try {
                    const response = await fetch(`http://localhost:8080/api/menu-items/${menuItemId}`, {
                        method: 'DELETE',
                        headers: {
                            'X-User-Role': 'Manager'
                        }
                    });
                    if (response.ok) {
                        alert('Xóa món thành công!');
                        switchView('manageProductView');
                    } else if (response.status === 403) {
                        alert('Bạn không có quyền thực hiện thao tác này.');
                    } else if (response.status === 404) {
                        alert('Không tìm thấy món để xóa.');
                    } else {
                        const errorText = await response.text();
                        alert(`Lỗi xóa món: ${response.status} - ${errorText}`);
                        console.error('Lỗi chi tiết:', errorText);
                    }
                } catch (error) {
                    console.error('Lỗi khi gửi yêu cầu xóa món:', error);
                    alert('Không thể kết nối đến server để xóa món.');
                }
            }
        });
    });
}


// --- Hàm hiển thị danh sách Nguyên liệu ---
function displayIngredients(ingredientsToDisplay) {
    const headerHtml = `
        <div class="ingredient-header">
            <span>Tên nguyên liệu</span>
            <span>Đơn vị</span>
            <span>Tồn kho hiện tại</span>
            <span>Mức tối thiểu</span>
        </div>
    `;
    ingredientListContainer.innerHTML = headerHtml;
    if (!ingredientsToDisplay || ingredientsToDisplay.length === 0) {
        ingredientListContainer.innerHTML += '<p style="padding: 10px; text-align: center; color: var(--text-color);">Không có nguyên liệu nào trong kho.</p>';
        return;
    }
    ingredientsToDisplay.forEach(ingredient => {
        const ingredientRow = document.createElement('div');
        ingredientRow.classList.add('ingredient-row');
        if (ingredient.currentStock <= ingredient.minStockLevel) {
            ingredientRow.classList.add('low-stock');
        }

        ingredientRow.innerHTML = `
            <span>${ingredient.ingredientName}</span>
            <span>${ingredient.unitOfMeasure}</span>
            <span>${ingredient.currentStock}</span>
            <span>${ingredient.minStockLevel}</span>
        `;
        ingredientListContainer.appendChild(ingredientRow);
    });
}

// --- Xử lý thêm món mới ---
addMenuItemForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const newMenuItem = {
        itemName: document.getElementById('newMenuItemName').value.trim(),
        description: document.getElementById('newMenuItemDescription')?.value.trim() || '',
        price: parseFloat(document.getElementById('newMenuItemPrice').value),
        category: document.getElementById('newMenuItemCategory').value.trim(),
        status: document.getElementById('newMenuItemStatus').value.trim(),
        isAvailable: document.getElementById('newMenuItemIsAvailable').checked,
        // imageUrl: '', Nếu cần ảnh thì thêm vào đây
        minSellingPrice: parseFloat(document.getElementById('newMenuItemMinSellingPrice').value || '0')
    };

    if (!newMenuItem.itemName || isNaN(newMenuItem.price) || !newMenuItem.category) {
        alert('Vui lòng điền đầy đủ thông tin hợp lệ.');
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/menu-items', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-User-Role': 'Manager'
            },
            body: JSON.stringify(newMenuItem)
        });

        if (response.ok) {
            const createdItem = await response.json();
            alert('Đã thêm món thành công!');
            closeModal(addMenuItemModal);
            switchView('manageProductView');
        } else {
            const err = await response.text();
            alert(`Lỗi khi thêm món: ${response.status} - ${err}`);
        }
    } catch (err) {
        console.error('Lỗi khi gửi yêu cầu thêm món:', err);
        alert('Không thể kết nối đến server để thêm món mới.');
    }
});


// --- Hàm chuyển đổi giữa các View ---
async function switchView(viewId) {
    document.querySelectorAll('.content-view').forEach(view => {
        view.classList.remove('active');
    });
    const targetView = document.getElementById(viewId);
    if (targetView) {
        targetView.classList.add('active');
        if (viewId === 'reportView') {
            await loadReportView();
        }
    } else {
        console.error(`Error: View with ID "${viewId}" not found in HTML.`);
        return;
    }

    menuButtons.forEach(button => {
        button.classList.remove('active');
        if (button.dataset.view === viewId) {
            button.classList.add('active');
        }
    });

    try {
        if (viewId === 'homeView' || viewId === 'manageProductView') {
            const response = await fetch('http://localhost:8080/api/menu-items');
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error('Network response was not ok: ' + response.status + ' ' + errorText);
            }
            menuItems = await response.json();
            if (viewId === 'homeView') {
                displayMenuItems(menuItems);
                document.querySelector('.category-button[data-category="all"]').classList.add('active');
            } else { // manageProductView
                displayProductMenuItems(menuItems);
            }
        } else if (viewId === 'storeView') {
            const response = await fetch('http://localhost:8080/api/ingredients');
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error('Network response was not ok: ' + response.status + ' ' + errorText);
            }
            ingredients = await response.json();
            displayIngredients(ingredients);
        } else {
            // Xử lý các View khác (báo cáo, đơn hàng, ...) nếu tương lai muốn thêm
        }
    } catch (error) {
        console.error(`Error fetching data for ${viewId}:`, error);
        alert(`Không thể tải dữ liệu cho ${viewId}: ${error.message}. Vui lòng kiểm tra backend và kết nối mạng.`);
    }
}

//Hàm hiển thị báo cáo
    async function loadReportView() {
        try {
            const res1 = await fetch('/api/reports/daily-sales');
            const res2 = await fetch('/api/reports/product-performance');
            const daily = await res1.json();
            const product = await res2.json();

            renderDailySalesTable(daily);
            renderProductPerformanceTable(product);
        } catch (err) {
            console.error("Error loading report:", err);
        }
    }
function renderDailySalesTable(data) {
    const tbody = document.querySelector('#dailySalesTable tbody');
    tbody.innerHTML = data.map(row => `
        <tr>
            <td>${row.saleDate}</td>
            <td>${row.numberOfOrders}</td>
            <td>${row.totalRevenueVND.toLocaleString('vi-VN')}</td>
            <td>${row.totalRevenueUSD.toFixed(2)}</td>
            <td>${row.totalRankDiscountGivenVND.toLocaleString('vi-VN')}</td>
            <td>${row.totalPromotionDiscountGivenVND.toLocaleString('vi-VN')}</td>
        </tr>
    `).join('');
}

function renderProductPerformanceTable(data) {
    const tbody = document.querySelector('#productSalesTable tbody');
    tbody.innerHTML = data.map(row => `
        <tr>
            <td>${row.itemName}</td>
            <td>${row.category}</td>
            <td>${row.totalQuantitySold}</td>
            <td>${row.grossRevenueVND.toLocaleString('vi-VN')}</td>
            <td>${row.grossRevenueUSD.toFixed(2)}</td>
            <td>${row.numberOfOrdersContainingItem}</td>
        </tr>
    `).join('');
}


// --- Hàm thêm sản phẩm vào giỏ hàng ---
function addItemToCart(item) {
    const existingItemIndex = cart.findIndex(cartItem => cartItem.menuItemId === item.menuItemId);

    if (existingItemIndex > -1) {
        cart[existingItemIndex].quantity++;
    } else {
        cart.push({
            menuItemId: item.menuItemId,
            itemName: item.itemName,
            price: item.price,
            quantity: 1
        });
    }
    updateCartDisplay();
}

// --- Hàm cập nhật hiển thị giỏ hàng ---
function updateCartDisplay() {
    const headerHtml = `
        <div class="cart-item-header">
            <span>Tên sản phẩm</span>
            <span>Giá</span>
            <span>SL</span>
            <span>Tổng</span>
            <span></span>
        </div>
    `;
    cartItemsContainer.innerHTML = headerHtml;

    let total = 0;
    cart.forEach(item => {
        const itemRow = document.createElement('div');
        itemRow.classList.add('cart-item-row');
        const itemTotal = item.price * item.quantity;
        total += itemTotal;

        itemRow.innerHTML = `
    <span>${item.itemName}</span>
    <span>${item.price.toLocaleString('vi-VN')}</span>
    <span>${item.quantity}</span>
    <span>${itemTotal.toLocaleString('vi-VN')} VND</span>
    <button class="remove-btn" data-id="${item.menuItemId}">X</button>`;

        cartItemsContainer.appendChild(itemRow);
    });

    cartTotalLabel.textContent = total.toLocaleString('vi-VN') + ' VND';

    document.querySelectorAll('.remove-btn').forEach(button => {
        button.addEventListener('click', (event) => {
            const itemId = Number(event.target.dataset.id);
            removeItemFromCart(itemId);
        });
    });
}

// --- Hàm xóa sản phẩm khỏi giỏ hàng ---
function removeItemFromCart(menuItemId) {
    cart = cart.filter(item => item.menuItemId !== menuItemId);
    updateCartDisplay();
}

// --- Hàm reset giỏ hàng và form khách hàng ---
function resetCartAndForm() {
    cart = [];
    updateCartDisplay();
    customerPhoneInput.value = '';
    customerNameInput.value = '';
    customerNameInput.readOnly = false;
    orderNotesInput.value = '';
    promotionCodeInput.value = '';
    appliedPromotionDisplay.textContent = '';
    appliedPromotionDisplay.classList.remove('active');
    selectedPromotionId = null;
    selectedPromotionName = null;
    currentCustomerId = null;
}

// --- Event Listeners ---

// Sidebar Menu Buttons
menuButtons.forEach(button => {
    button.addEventListener('click', () => {
        const viewId = button.dataset.view;
        if (viewId) {
            switchView(viewId);
        }
    });
});

// Category Buttons (for Menu View)
categoryButtons.forEach(button => {
    button.addEventListener('click', () => {
        categoryButtons.forEach(btn => btn.classList.remove('active'));
        button.classList.add('active');

        const category = button.dataset.category;
        if (category === 'all') {
            displayMenuItems(menuItems);
        } else {
            const filteredItems = menuItems.filter(item => item.category === category);
            displayMenuItems(filteredItems);
        }
    });
});

// Search Customer Button
searchCustomerBtn.addEventListener('click', async () => {
    const phoneNumber = customerPhoneInput.value.trim();
    if (!phoneNumber) {
        alert('Vui lòng nhập số điện thoại để tìm kiếm.');
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/customers/search?phoneNumber=${encodeURIComponent(phoneNumber)}`);

        if (response.ok) {
            const customer = await response.json();
            currentCustomerId = customer.customerId;
            customerNameInput.value = customer.customerName;
            customerNameInput.readOnly = true;
            alert(`Tìm thấy khách hàng: ${customer.customerName} (ID: ${customer.customerId})`);
        } else if (response.status === 404) {
            currentCustomerId = null;
            customerNameInput.value = '';
            customerNameInput.readOnly = false;
            alert(`Không tìm thấy khách hàng với SĐT ${phoneNumber}. Vui lòng nhập Tên Khách hàng mới để tạo.`);
            customerNameInput.focus();
        } else {
            const errorText = await response.text();
            alert(`Lỗi tìm kiếm khách hàng: ${response.status} - ${errorText}`);
            console.error('Lỗi tìm kiếm khách hàng chi tiết:', errorText);
        }
    } catch (error) {
        console.error('Lỗi khi tìm kiếm khách hàng:', error);
        alert('Không thể kết nối đến server backend để tìm khách hàng.');
    }
});

// Nút Áp dụng khuyến mãi
applyPromotionBtn.addEventListener('click', async () => {
    const promotionId = promotionCodeInput.value.trim();
    if (!promotionId) {
        alert('Vui lòng nhập ID khuyến mãi.');
        return;
    }

    try {
        // Gọi API để lấy thông tin khuyến mãi
        const response = await fetch(`http://localhost:8080/api/promotions/${promotionId}`);
        if (response.ok) {
            const promotion = await response.json();
            // Kiểm tra ngày hết hạn khuyến mãi
            const today = new Date();
            const endDate = new Date(promotion.endDate);
            if (today > endDate) {
                alert(`Khuyến mãi "${promotion.promotionName}" đã hết hạn vào ngày ${promotion.endDate}.`);
                selectedPromotionId = null;
                selectedPromotionName = null;
                appliedPromotionDisplay.textContent = 'Khuyến mãi đã hết hạn.';
                appliedPromotionDisplay.classList.remove('active');
                return;
            }

            // Kiểm tra số lượt sử dụng còn lại
            if (promotion.remainingUses !== null && promotion.remainingUses <= 0) {
                alert(`Khuyến mãi "${promotion.promotionName}" đã hết lượt sử dụng.`);
                selectedPromotionId = null;
                selectedPromotionName = null;
                appliedPromotionDisplay.textContent = 'Khuyến mãi đã hết lượt sử dụng.';
                appliedPromotionDisplay.classList.remove('active');
                return;
            }

            // Áp dụng khuyến mãi thành công
            selectedPromotionId = promotion.promotionId;
            selectedPromotionName = promotion.promotionName;
            appliedPromotionDisplay.textContent = `Áp dụng: ${promotion.promotionName}`;
            appliedPromotionDisplay.classList.add('active');
            alert(`Đã áp dụng khuyến mãi: "${promotion.promotionName}"`);

        } else if (response.status === 404) {
            alert(`Không tìm thấy khuyến mãi với ID: ${promotionId}.`);
            selectedPromotionId = null;
            selectedPromotionName = null;
            appliedPromotionDisplay.textContent = '';
            appliedPromotionDisplay.classList.remove('active');
        } else {
            const errorText = await response.text();
            alert(`Lỗi khi áp dụng khuyến mãi: ${response.status} - ${errorText}`);
            console.error('Lỗi chi tiết:', errorText);
            selectedPromotionId = null;
            selectedPromotionName = null;
            appliedPromotionDisplay.textContent = '';
            appliedPromotionDisplay.classList.remove('active');
        }
    } catch (error) {
        console.error('Lỗi kết nối khi áp dụng khuyến mãi:', error);
        alert('Không thể kết nối đến server để áp dụng khuyến mãi.');
        selectedPromotionId = null;
        selectedPromotionName = null;
        appliedPromotionDisplay.textContent = '';
        appliedPromotionDisplay.classList.remove('active');
    }
});


// Checkout Button (calls backend API)
checkoutBtn.addEventListener('click', async () => {
    if (cart.length === 0) {
        alert('Giỏ hàng trống!');
        return;
    }

    const phoneNumber = customerPhoneInput.value.trim();
    const customerName = customerNameInput.value.trim();
    const orderNotes = orderNotesInput.value.trim();

    if (!phoneNumber) {
        alert('Vui lòng nhập số điện thoại.');
        return;
    }

    try {
        if (currentCustomerId === null) {
            if (!customerName) {
                alert("Vui lòng nhập tên khách hàng để tạo mới.");
                return;
            }

            const createCustomerRes = await fetch('http://localhost:8080/api/customers', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    phoneNumber: phoneNumber,
                    customerName: customerName,
                })
            });

            if (createCustomerRes.ok) {
                const newCustomer = await createCustomerRes.json();
                currentCustomerId = newCustomer.customerId;
                alert(`Đã tạo khách hàng mới: ${newCustomer.customerName} (ID: ${newCustomer.customerId})`);
            } else if (createCustomerRes.status === 409) {
                alert('Khách hàng với số điện thoại này đã tồn tại. Vui lòng bấm "Tìm" để sử dụng khách hàng cũ hoặc nhập số điện thoại khác.');
                return;
            } else {
                const errorText = await createCustomerRes.text();
                alert(`Không thể tạo khách hàng mới: ${createCustomerRes.status} - ${errorText}`);
                console.error('Lỗi tạo khách hàng mới chi tiết:', errorText);
                return;
            }
        }

        const orderDetails = cart.map(item => ({
            menuItemId: item.menuItemId,
            quantity: item.quantity
        }));

        const orderRequest = {
            customerId: currentCustomerId,
            promotionId: selectedPromotionId, // Gửi ID khuyến mãi đã chọn (có thể là null)
            expectedPickupTime: new Date(Date.now() + 60 * 60 * 1000).toISOString(),
            paymentMethod: "Cash",
            notes: orderNotes, // Gửi ghi chú đơn hàng
            orderDetails: orderDetails
        };

        console.log("Sending order request:", orderRequest);

        const response = await fetch('http://localhost:8080/api/customerorders', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-User-Role': 'Manager'
            },
            body: JSON.stringify(orderRequest)
        });

        if (response.ok) {
            let orderConfirmation;
            try {
                orderConfirmation = await response.json();
            } catch (e) {
                alert("Đặt hàng thành công! (Không nhận được phản hồi chi tiết từ server, có thể do lỗi parsing JSON response)");
                resetCartAndForm();
                return;
            }

            const totalAmountText = orderConfirmation.totalAmount
                ? orderConfirmation.totalAmount.toLocaleString('vi-VN')
                : 'Không xác định';

            alert(`Đặt hàng thành công! OrderID: ${orderConfirmation.orderId}\nTổng tiền: ${totalAmountText} VND`);
            resetCartAndForm();
        } else {
            const errorBody = await response.text();
            alert(`Lỗi đặt hàng: ${response.status} - ${errorBody}`);
            console.error("Lỗi chi tiết từ server:", errorBody);
        }

    } catch (error) {
        console.error('Lỗi khi gửi yêu cầu đặt hàng:', error);
        alert('Không thể kết nối đến server backend hoặc lỗi mạng.');
    }
});


// --- Modal Functionality ---
function openModal(modal) {
    modal.style.display = 'flex';
}

function closeModal(modal) {
    modal.style.display = 'none';
}

// Add New Menu Item Modal
addMenuItemBtn.addEventListener('click', () => {
    addMenuItemForm.reset();
    document.getElementById('newMenuItemIsAvailable').checked = true;
    openModal(addMenuItemModal);
});
closeMenuItemModal.addEventListener('click', () => closeModal(addMenuItemModal));
window.addEventListener('click', (event) => {
    if (event.target === addMenuItemModal) {
        closeModal(addMenuItemModal);
    }
});

addMenuItemForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const newMenuItem = {
        itemName: document.getElementById('newMenuItemName').value.trim(),
        description: document.getElementById('newMenuItemDescription').value.trim(),
        price: parseFloat(document.getElementById('newMenuItemPrice').value),
        category: document.getElementById('newMenuItemCategory').value.trim(),
        status: document.getElementById('newMenuItemStatus').value.trim(),
        isAvailable: document.getElementById('newMenuItemIsAvailable').checked,
        imageUrl: document.getElementById('newMenuItemImageUrl').value.trim(),
        minSellingPrice: parseFloat(document.getElementById('newMenuItemMinSellingPrice').value || '0')
    };

    if (!newMenuItem.itemName || !newMenuItem.price || !newMenuItem.category || isNaN(newMenuItem.price)) {
        alert('Vui lòng điền đủ Tên món, Giá và Danh mục. Giá phải là số.');
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/menu-items', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-User-Role': 'Manager'
            },
            body: JSON.stringify(newMenuItem)
        });

        if (response.ok) {
            const createdItem = await response.json();
            alert('Thêm món mới thành công! ID: ' + createdItem.menuItemId);
            closeModal(addMenuItemModal);
            switchView('manageProductView');
        } else if (response.status === 403) {
            alert('Bạn không có quyền thêm món mới. Chỉ Manager mới có thể thực hiện.');
        } else if (response.status === 409) {
            alert('Món đã tồn tại với tên này.');
        }
        else {
            const errorText = await response.text();
            alert(`Lỗi thêm món mới: ${response.status} - ${errorText}`);
            console.error("Lỗi chi tiết:", errorText);
        }
    } catch (error) {
        console.error('Lỗi khi gửi yêu cầu thêm món:', error);
        alert('Không thể kết nối đến server để thêm món mới.');
    }
});


// Add/Restock Ingredient Modal
addRestockIngredientBtn.addEventListener('click', () => {
    addRestockIngredientForm.reset();
    ingredientIdFound.value = '';
    ingredientCurrentStock.value = '';
    ingredientUnit.value = '';
    ingredientUnit.readOnly = false;
    ingredientMinStockLevel.value = '';
    ingredientMinStockLevel.readOnly = false;
    ingredientQuantityChange.value = '';
    submitIngredientFormBtn.textContent = 'Lưu';
    openModal(addRestockIngredientModal);
});
closeIngredientModal.addEventListener('click', () => closeModal(addRestockIngredientModal));
window.addEventListener('click', (event) => {
    if (event.target === addRestockIngredientModal) {
        closeModal(addRestockIngredientModal);
    }
});

searchIngredientBtn.addEventListener('click', async () => {
    const nameToSearch = ingredientNameSearch.value.trim();
    if (!nameToSearch) {
        alert('Vui lòng nhập tên nguyên liệu để tìm kiếm.');
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/ingredients/search?name=${encodeURIComponent(nameToSearch)}`);

        if (response.ok) {
            const ingredient = await response.json();
            ingredientIdFound.value = ingredient.ingredientId;
            ingredientNameSearch.value = ingredient.ingredientName;
            ingredientUnit.value = ingredient.unitOfMeasure;
            ingredientUnit.readOnly = true;
            ingredientCurrentStock.value = ingredient.currentStock;
            ingredientMinStockLevel.value = ingredient.minStockLevel;
            ingredientMinStockLevel.readOnly = true;
            submitIngredientFormBtn.textContent = 'Nhập kho';
            alert(`Tìm thấy nguyên liệu: ${ingredient.ingredientName}. Chỉ cần nhập Số lượng thay đổi để nhập kho.`);
        } else if (response.status === 404) {
            ingredientIdFound.value = '';
            ingredientUnit.value = '';
            ingredientUnit.readOnly = false;
            ingredientCurrentStock.value = '0';
            ingredientMinStockLevel.value = '0';
            ingredientMinStockLevel.readOnly = false;
            submitIngredientFormBtn.textContent = 'Thêm mới';
            alert(`Không tìm thấy nguyên liệu "${nameToSearch}". Bạn có thể thêm mới.`);
        } else {
            const errorText = await response.text();
            alert(`Lỗi tìm kiếm nguyên liệu: ${response.status} - ${errorText}`);
            console.error("Lỗi chi tiết:", errorText);
        }
    } catch (error) {
        console.error('Lỗi khi gửi yêu cầu nguyên liệu:', error);
        alert('Không thể kết nối đến server để tìm nguyên liệu.');
    }
});


addRestockIngredientForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const ingredientId = ingredientIdFound.value.trim();
    const isNewIngredient = !ingredientId;
    const role = 'Manager';

    const name = ingredientNameSearch.value.trim();
    const unit = ingredientUnit.value.trim();
    const quantityChange = parseFloat(ingredientQuantityChange.value || '0');
    const minStock = parseFloat(ingredientMinStockLevel.value || '0');

    if (!name || !unit) {
        alert("Vui lòng nhập đầy đủ Tên nguyên liệu và Đơn vị.");
        return;
    }

    if (isNaN(quantityChange) || quantityChange === 0) {
        alert("Số lượng thay đổi không hợp lệ. Phải là số và khác 0.");
        return;
    }

    if (isNewIngredient) {
        // Trường hợp thêm mới nguyên liệu
        const body = {
            ingredientName: name,
            unitOfMeasure: unit,
            currentStock: quantityChange,
            minStockLevel: isNaN(minStock) ? 0 : minStock
        };

        try {
            const response = await fetch('http://localhost:8080/api/ingredients', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-User-Role': role
                },
                body: JSON.stringify(body)
            });

            if (response.ok) {
                const result = await response.json();
                alert(`✅ Thêm nguyên liệu thành công: ${result.ingredientName} (ID: ${result.ingredientId})`);
                closeModal(addRestockIngredientModal);
                switchView('storeView');
            } else {
                const errorText = await response.text();
                alert(`❌ Lỗi khi thêm nguyên liệu: ${response.status} - ${errorText}`);
                console.error("Chi tiết:", errorText);
            }
        } catch (err) {
            console.error("Lỗi mạng khi thêm nguyên liệu:", err);
            alert("Không thể kết nối đến server khi thêm nguyên liệu.");
        }

    } else {
        // Trường hợp nhập kho nguyên liệu có sẵn
        const url = `http://localhost:8080/api/ingredients/${ingredientId}/stock?quantityChange=${quantityChange}`;

        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'X-User-Role': role
                }
            });

            if (response.ok) {
                const result = await response.json();
                alert(`✅ Nhập kho thành công! Tồn kho mới: ${result.currentStock}`);
                closeModal(addRestockIngredientModal);
                switchView('storeView');
            } else {
                const errorText = await response.text();
                alert(`❌ Lỗi nhập kho: ${response.status} - ${errorText}`);
                console.error("Chi tiết:", errorText);
            }
        } catch (err) {
            console.error("Lỗi mạng khi nhập kho:", err);
            alert("Không thể kết nối đến server khi nhập kho.");
        }
    }
});



// --- Initial Load ---
document.addEventListener('DOMContentLoaded', () => {
    resetCartAndForm();
    switchView('homeView');
});
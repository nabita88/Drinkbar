/* kiosk.js --------------------------------------------------------------- */
const AppConfig={
    apiEndpoint:/*[[@{/api}]]*/'/api',
    activeCategoryId:/*[[${currentCategoryId}]]*/'COFFEE',
    branchNo:window.kioskConfig?.branchNo || 1
};

/* simple helpers */
const $=id=>document.getElementById(id);
const $$=sel=>document.querySelectorAll(sel);
const fmt=n=>n.toLocaleString()+'원';

/* API helper */
async function apiCall(endpoint,options={}){
    const defaultHeaders={'Content-Type':'application/json'};
    const response=await fetch(`${AppConfig.apiEndpoint}${endpoint}`,{
        ...options,
        headers:{...defaultHeaders,...options.headers}
    });
    return await response.json();
}

/* runtime state */
let cart=new Map();
let basketItemMap=new Map();
let productOptionsCache=new Map();

/* ---------------- Category ---------------- */
async function switchCategory(categoryCode, targetElement = null){
    const grid=$('catalogGrid');
    if(!grid)return;

    grid.innerHTML = '<div style="text-align:center; padding:50px; color:#666;">상품을 불러오는 중...</div>';

    const result = await apiCall(`/products/list?category=${categoryCode}&branchNo=${AppConfig.branchNo}`);

    if (result && result.dtoList) {
        updateMenuGrid(result.dtoList);
    }

    // 모든 탭 비활성화
    $$('.tab').forEach(tb=>tb.classList.remove('active'));

    // 활성화할 탭 찾기
    if(targetElement) {
        // 클릭 이벤트로 호출된 경우
        targetElement.classList.add('active');
    } else {
        // 프로그래밍 방식으로 호출된 경우 - 텍스트로 탭 찾기
        $$('.tab').forEach(tab => {
            if(tab.textContent === getCategoryName(categoryCode)) {
                tab.classList.add('active');
            }
        });
    }

    AppConfig.activeCategoryId=categoryCode;
}

function getCategoryName(categoryCode) {
    const categoryNames = {
        'COFFEE': '커피',
        'TEA': '차',
        'DESSERT': '디저트',
        'BAKERY': '베이커리',
        'BEVERAGE': '음료',
        'FOOD': '푸드'
    };
    return categoryNames[categoryCode] || '커피';
}

// getProductEmoji를 getCategoryName으로 통합
function getProductEmoji(category) {
    return getCategoryName(category);
}

function updateMenuGrid(products) {
    const grid = $('catalogGrid');
    if (!grid) return;

    if (!products || products.length === 0) {
        grid.innerHTML = '<div style="text-align:center; padding:50px; color:#666;">등록된 상품이 없습니다.</div>';
        return;
    }

    // 테이블 구조는 유지하되 더 안전한 방식으로 생성
    const table = document.createElement('table');
    table.className = 'coffee-menu-table';
    const tbody = document.createElement('tbody');

    for (let i = 0; i < products.length; i += 6) {
        const tr = document.createElement('tr');

        for (let j = 0; j < 6; j++) {
            const idx = i + j;
            const td = document.createElement('td');

            if (idx < products.length) {
                const product = products[idx];
                td.className = 'menu-item';
                td.dataset.productNo = product.productNo;
                td.dataset.productName = product.productName;
                td.dataset.productPrice = product.sellingPrice;

                const iconDiv = document.createElement('div');
                iconDiv.className = 'menu-icon';
                iconDiv.textContent = getCategoryName(product.category || AppConfig.activeCategoryId);

                const nameDiv = document.createElement('div');
                nameDiv.className = 'menu-name';
                nameDiv.textContent = product.productName;

                const priceDiv = document.createElement('div');
                priceDiv.className = 'menu-price';
                priceDiv.textContent = product.sellingPrice.toLocaleString() + '원';

                td.appendChild(iconDiv);
                td.appendChild(nameDiv);
                td.appendChild(priceDiv);
            } else {
                td.className = 'empty-cell';
            }

            tr.appendChild(td);
        }

        tbody.appendChild(tr);
    }

    table.appendChild(tbody);
    grid.innerHTML = '';
    grid.appendChild(table);

    // 이벤트 위임으로 클릭 처리
    table.addEventListener('click', (e) => {
        const menuItem = e.target.closest('.menu-item');
        if (menuItem) {
            const productNo = parseInt(menuItem.dataset.productNo);
            addItemToCart(productNo);
        }
    });
}

/* ---------------- Cart (장바구니) ---------------- */
let selectedProduct = null;

async function addItemToCart(mid){
    // data 속성에서 정보 가져오기
    const clickedItem = document.querySelector(`[data-product-no="${mid}"]`);
    if (!clickedItem) return;

    const menuName = clickedItem.dataset.productName;
    const menuPrice = parseInt(clickedItem.dataset.productPrice);

    // 선택 효과
    clickedItem.classList.add('selected');
    setTimeout(() => clickedItem.classList.remove('selected'), 400);

    if (!menuName || !menuPrice) return;

    if (AppConfig.activeCategoryId === 'COFFEE' || AppConfig.activeCategoryId === 'BEVERAGE') {
        selectedProduct = {productNo: mid, name: menuName, price: menuPrice};
        await showOptionsModal();
    } else {
        addToBasketWithOptions(mid, menuName, menuPrice, null, null, '', 0);
    }
}

async function fetchProductOptions(productNo) {
    if (productOptionsCache.has(productNo)) {
        return productOptionsCache.get(productNo);
    }

    const result = await apiCall(`/products/${productNo}`);
    if (result && result.options) {
        productOptionsCache.set(productNo, result.options);
        return result.options;
    }

    return {
        sizes: ['LARGE', 'MEGA'],  // 기본값을 LARGE, MEGA로 변경
        temperatures: ['HOT', 'COLD']
    };
}

async function showOptionsModal() {
    const options = await fetchProductOptions(selectedProduct.productNo);

    const availableSizes = options.sizes || ['TALL', 'GRANDE'];
    const availableTemps = options.temperatures || ['HOT', 'COLD'];

    const modalContent = createOptionsModalContent(availableSizes, availableTemps);

    showCustomModal(modalContent);
}

function createOptionsModalContent(sizes, temperatures) {
    const sizeNames = {
        // 커피는 LARGE, MEGA만 사용
        'LARGE': '라지',
        'MEGA': '메가',
        // 기존 사이즈들은 주석 처리
        /*
        'SHORT': '숏',
        'TALL': '톨',
        'GRANDE': '그란데',
        'VENTI': '벤티'
        */
    };

    const tempNames = {
        'HOT': '뜨거운',
        'COLD': '차가운'
    };

    const sizeOptions = sizes.map(size =>
        `<label><input type="radio" name="size" value="${size}" ${size === sizes[0] ? 'checked' : ''}>
         ${sizeNames[size] || size}</label>`
    ).join('');

    const tempOptions = temperatures.map(temp =>
        `<label><input type="radio" name="temperature" value="${temp}" ${temp === temperatures[0] ? 'checked' : ''}>
         ${tempNames[temp] || temp}</label>`
    ).join('');

    return `
        <div class="options-modal">
            <h3>${selectedProduct.name}</h3>
            <div class="option-group">
                <h4>사이즈 선택</h4>
                ${sizeOptions}
            </div>
            <div class="option-group">
                <h4>온도 선택</h4>
                ${tempOptions}
            </div>
            <div class="modal-buttons">
                <button onclick="confirmOptions()">확인</button>
                <button onclick="closeModal()">취소</button>
            </div>
        </div>
    `;
}

function showCustomModal(content) {
    const overlay = document.createElement('div');
    overlay.id = 'modalOverlay';
    overlay.className = 'modal-overlay';
    overlay.innerHTML = `
        <div class="modal-content">
            ${content}
        </div>
    `;
    document.body.appendChild(overlay);
}

function closeModal() {
    const overlay = document.getElementById('modalOverlay');
    if (overlay) {
        overlay.remove();
    }
}

function confirmOptions() {
    const selectedSize = document.querySelector('input[name="size"]:checked')?.value || 'LARGE';  // 기본값 LARGE로 변경
    const selectedTemp = document.querySelector('input[name="temperature"]:checked')?.value || 'COLD';

    addToBasketWithOptions(
        selectedProduct.productNo,
        selectedProduct.name,
        selectedProduct.price,
        selectedSize,
        selectedTemp,
        '',
        0
    );

    closeModal();
}

async function addToBasketWithOptions(productNo, productName, price, size, temperature, additionalOptions, additionalPrice) {
    await addToBasketAPI(productNo, productName, price, size, temperature, additionalOptions, additionalPrice);

    const current = cart.get(productNo) || {qty:0, name:productName, cost:price};
    current.qty++;
    cart.set(productNo, current);

    renderCart();
}

async function addToBasketAPI(productNo, productName, price, size, temperature, additionalOptions, additionalPrice){
    const requestBody = {
        productNo: productNo,
        quantity: 1
    };

    if (size) requestBody.size = size;
    if (temperature) requestBody.temperature = temperature;
    if (additionalOptions) requestBody.additionalOptions = additionalOptions;
    if (additionalPrice) requestBody.additionalPrice = additionalPrice;

    const result = await apiCall(`/basket/branch/${AppConfig.branchNo}/add?email=user0@example.com`, {
        method: 'POST',
        body: JSON.stringify(requestBody)
    });

    await refreshBasketItemMap();
}

async function refreshBasketItemMap() {
    const items = await apiCall(`/basket/items?email=user0@example.com&branchNo=${AppConfig.branchNo}`);

    basketItemMap.clear();
    if (Array.isArray(items)) {
        items.forEach(item => {
            if (item.productNo && item.basketGoodsNo) {
                basketItemMap.set(item.productNo, item.basketGoodsNo);
            }
        });
    }
}

async function removeFromBasketAPI(basketGoodsNo){
    await apiCall(`/basket/${basketGoodsNo}?email=user0@example.com`,{
        method:'DELETE'
    });
}

function calcTotal(){return[...cart.values()].reduce((s,i)=>s+i.qty*i.cost,0);}

function renderCart(){
    const empty=$('emptyCartMessage');
    const holder=$('basketItemsHolder');
    const summary=$('basketSummary');

    if(cart.size===0){
        if(empty)empty.style.display='flex';
        if(holder)holder.innerHTML='';
        if(summary)summary.style.display='none';
        return;
    }
    if(empty)empty.style.display='none';

    holder.innerHTML=[...cart.entries()].map(([id,o])=>`
        <article class="order-item fade-in" data-cart-item-id="${id}">
            <div class="item-info">
                <h4 class="item-name">${o.name}</h4>
                <p  class="item-price">${fmt(o.cost)}</p>
            </div>
            <div class="quantity-controls">
                <button class="qty-btn" aria-label="수량 감소" onclick="decrementItemQuantity(${id})">-</button>
                <span   class="qty-display">${o.qty}</span>
                <button class="qty-btn" aria-label="수량 증가" onclick="incrementItemQuantity(${id})">+</button>
            </div>
        </article>
    `).join('');

    const total=fmt(calcTotal());
    summary.style.display='block';
    summary.innerHTML=`
        <div class="total-amount">총 ${total}</div>
        <button class="checkout-btn" onclick="processCheckout()">
            주문하기
        </button>`;
}

async function incrementItemQuantity(productNo) {
    const item = cart.get(productNo);
    if (item) {
        await addToBasketAPI(productNo, item.name, item.cost, null, null, '', 0);
        item.qty++;
        cart.set(productNo, item);
        renderCart();
    }
}

async function decrementItemQuantity(productNo) {
    const item = cart.get(productNo);
    if (item && item.qty > 1) {
        item.qty--;
        cart.set(productNo, item);
        renderCart();
    } else if (item && item.qty === 1) {
        const basketGoodsNo = basketItemMap.get(productNo);
        if (basketGoodsNo) {
            await removeFromBasketAPI(basketGoodsNo);
        }
        cart.delete(productNo);
        basketItemMap.delete(productNo);
        renderCart();
    }
}

/* ---------------- Checkout (결제) ---------------- */
async function processCheckout(){
    if(cart.size===0){
        alert('장바구니가 비어있습니다.');
        return;
    }

    const totalAmount=calcTotal();
    if(!confirm('총 '+fmt(totalAmount)+'을 결제하시겠습니까?'))return;

    const checkoutBtn=document.querySelector('.checkout-btn');
    if(checkoutBtn){
        checkoutBtn.disabled=true;
        checkoutBtn.textContent='주문 처리중...';
    }

    const orderResult=await createOrderFromBasket();

    if(orderResult && orderResult.data && orderResult.data.orderNo){
        const orderNo=orderResult.data.orderNo;
        alert(`주문이 완료되었습니다!\n주문번호: ${orderNo}`);
        await emptyCart();
    }

    if(checkoutBtn){
        checkoutBtn.disabled=false;
        checkoutBtn.textContent='주문하기';
    }
}

async function createOrderFromBasket(){
    const pickupTime=new Date();
    pickupTime.setMinutes(pickupTime.getMinutes()+30);

    const requestBody = {
        requestedPickupTime:pickupTime.toISOString(),
        specialRequests:''
    };

    const response = await apiCall('/orders/from-basket-order?email=user0@example.com',{
        method:'POST',
        body:JSON.stringify(requestBody)
    });

    return response;
}

/* ---------------- Actions ---------------- */
async function emptyCart(){
    const deletePromises = [];
    for(let [productNo, item] of cart){
        if(basketItemMap.has(productNo)){
            const basketGoodsNo = basketItemMap.get(productNo);
            deletePromises.push(removeFromBasketAPI(basketGoodsNo));
        }
    }

    if(deletePromises.length > 0){
        await Promise.all(deletePromises);
    }

    cart.clear();
    basketItemMap.clear();
    renderCart();
}

/* ---------------- Init ---------------- */
document.addEventListener('DOMContentLoaded',()=>{
    loadBranchInfo();
    initializeBasket();

    setTimeout(() => {
        switchCategory('COFFEE', null);
    }, 100);
});

async function loadBranchInfo() {
    const urlParams = new URLSearchParams(window.location.search);
    const branchTitle = urlParams.get('branchTitle');

    if (branchTitle) {
        const branchNameElement = document.getElementById('branchName');
        if (branchNameElement) {
            branchNameElement.textContent = `- ${branchTitle}`;
        }
    } else {
        const branchNo = AppConfig.branchNo;
        const response = await fetch(`/api/v1/Branches/${branchNo}`);

        if (response.ok) {
            const result = await response.json();

            if (result.success && result.data && result.data.branchTitle) {
                const branchNameElement = document.getElementById('branchName');
                if (branchNameElement) {
                    branchNameElement.textContent = `- ${result.data.branchTitle}`;
                }
            }
        }
    }
}

async function initializeBasket() {
    const items = await apiCall(`/basket/items?email=user0@example.com&branchNo=${AppConfig.branchNo}`);

    cart.clear();
    basketItemMap.clear();

    if (Array.isArray(items) && items.length > 0) {
        items.forEach(item => {
            cart.set(item.productNo, {
                qty: item.quantity,
                name: item.productName,
                cost: item.sellingPrice
            });
            basketItemMap.set(item.productNo, item.basketGoodsNo);
        });
        renderCart();
    }
}
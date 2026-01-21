var stompClient = null;

/**
 * Основная функция подключения.
 * Вызывается автоматически на всех страницах.
 * @param {Function} onConnectedCallback - (Опционально) Функция для доп. подписок (например, для видео-комнаты)
 */
function connectGlobalSocket(onConnectedCallback) {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    // Отключаем лишний шум в консоли, если нужно
    // stompClient.debug = null;

    stompClient.connect({}, function (frame) {
        console.log('✅ Global WebSocket Connected');

        // 1. Подписка на личные уведомления (работает на ВСЕХ страницах)
        stompClient.subscribe('/user/queue/notifications', function (result) {
            var notification = JSON.parse(result.body);
            handleGlobalNotification(notification);
        });

        // 2. Если передан коллбек (например, мы в комнате видео), запускаем его
        if (onConnectedCallback) {
            onConnectedCallback(stompClient);
        }

    }, function(error) {
        console.log("❌ STOMP error " + error);
        // Можно добавить логику переподключения через 5 секунд
        setTimeout(() => connectGlobalSocket(onConnectedCallback), 5000);
    });
}

/**
 * Обработчик входящего уведомления
 */
function handleGlobalNotification(notification) {
    // 1. Проверка: Если мы сейчас находимся в чате с этим человеком, то уведомление не нужно
    // (переменная conversationId обычно есть на странице чата)
    if (typeof conversationId !== 'undefined' && notification.conversationId == conversationId) {
        return;
    }

    // 2. Показываем всплывашку (Toast)
    showGlobalToast(notification);

    // 3. Обновляем счетчик (Badge)
    updateGlobalBadge();

    // 4. (Опционально) Добавляем в список уведомлений в сайдбаре
    addToSidebarList(notification);
}

/**
 * Логика отображения Bootstrap Toast (взята из вашего фрагмента)
 */
function showGlobalToast(notification) {
    var bodyEl = document.getElementById('global-toast-body');
    var linkEl = document.getElementById('global-toast-link');

    // Настраиваем текст (поддержка полей message или content)
    if(bodyEl) {
        var msgText = notification.message || notification.content || "Sent a message";
        bodyEl.innerText = notification.senderUsername + ": " + msgText;
    }

    // Настраиваем ссылку
    if(linkEl && notification.conversationId) {
        linkEl.href = "/chats/" + notification.conversationId;
    }

    // Показываем Toast
    var toastEl = document.getElementById('liveToast');
    if(toastEl && typeof bootstrap !== 'undefined') {
        var toast = new bootstrap.Toast(toastEl);
        toast.show();
    }
}

/**
 * Логика обновления красного бейджика
 */
function updateGlobalBadge() {
    // Проверяем оба ID (из вашего старого кода и нового sidebar)
    var badges = [
        document.getElementById("nav-notification-badge"),
        document.getElementById("notificationBadge")
    ];

    badges.forEach(function(badge) {
        if (badge) {
            var currentCount = parseInt(badge.innerText) || 0;
            badge.innerText = currentCount + 1;
            badge.style.display = 'block'; // Или 'inline-block'

            // Анимация "подпрыгивания" (опционально)
            badge.classList.add('bg-danger');
        }
    });
}

/**
 * Добавление в выпадающий список (из логики сайдбара)
 */
function addToSidebarList(notification) {
    var notifList = document.getElementById("notificationList");
    var noMsgItem = document.getElementById("no-notif-msg");

    if (notifList) {
        if (noMsgItem) noMsgItem.style.display = "none";

        var li = document.createElement("li");
        li.innerHTML = `
            <a href="/chats/${notification.conversationId}" class="dropdown-item p-2 border-bottom border-secondary text-light">
                <div class="d-flex align-items-center">
                    <div class="flex-grow-1 overflow-hidden">
                        <strong class="small text-info">${notification.senderUsername}</strong>
                        <div class="text-truncate small text-secondary" style="max-width: 200px;">
                            ${notification.message || notification.content || 'New message'}
                        </div>
                    </div>
                    <div class="ms-2"><span class="badge bg-primary rounded-circle p-1" style="width: 8px; height: 8px; display:block;"></span></div>
                </div>
            </a>
        `;
        var header = notifList.querySelector(".dropdown-header").parentNode;
        if (header && header.nextSibling) {
            notifList.insertBefore(li, header.nextSibling);
        } else {
            notifList.appendChild(li);
        }
    }
}

// Автозапуск при загрузке страницы
document.addEventListener("DOMContentLoaded", function() {
    // Если мы НЕ на странице видео (где есть своя логика подключения), подключаемся сразу
    // Проверка простая: если функция connectGlobalSocket не вызвана вручную, вызываем её
    // Но чтобы не конфликтовать, лучше просто вызвать её,
    // а на странице видео (room.html) вызывать её же, но с коллбеком.

    // Простейший вариант:
    if (typeof manualConnection === 'undefined') {
        connectGlobalSocket();
    }
});
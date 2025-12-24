var notificationStompClient = null;

document.addEventListener("DOMContentLoaded", function() {
    connectGlobalNotifications();
});

function connectGlobalNotifications() {
    var socket = new SockJS('/ws');
    notificationStompClient = Stomp.over(socket);

    // Disable debug logs for the global client to keep console clean
    notificationStompClient.debug = null;

    notificationStompClient.connect({}, function (frame) {

        // SUBSCRIBE TO PRIVATE NOTIFICATIONS
        // Spring translates "/user/queue/notifications" to the specific user's session
        notificationStompClient.subscribe('/user/queue/notifications', function (result) {
            showNotificationPopup(JSON.parse(result.body));
        });

    });
}

function showNotificationPopup(notification) {
    // 1. Check if we are already on the chat page for this conversation
    // If yes, we might not want to show a popup (optional)
    var currentChatIdInput = document.getElementById("currentConversationId");
    if (currentChatIdInput && currentChatIdInput.value == notification.conversationId) {
        return; // Don't show popup if I'm looking at the chat
    }

    // 2. Update a Badge Counter (e.g., in Navbar)
    var badge = document.getElementById("notificationBadge");
    if (badge) {
        var count = parseInt(badge.innerText) || 0;
        badge.innerText = count + 1;
        badge.style.display = 'inline-block';
    }

    // 3. Show a Toast / Popup (Bootstrap Toast)
    // You need to add the HTML for this toast in your layout (see Step 3)
    var toastEl = document.getElementById('liveToast');
    var toastBody = document.getElementById('toastBody');

    if (toastEl && toastBody) {
        toastBody.innerText = notification.content;
        var toast = new bootstrap.Toast(toastEl);
        toast.show();
    }
}
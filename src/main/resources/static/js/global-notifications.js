var globalStompClient = null;

document.addEventListener("DOMContentLoaded", function() {
    connectGlobalNotifications();
});

function connectGlobalNotifications() {
    // 1. Connect to WebSocket
    var socket = new SockJS('/ws');
    globalStompClient = Stomp.over(socket);
    globalStompClient.debug = null; // Disable debug logs

    globalStompClient.connect({}, function (frame) {
        console.log('Global Notifications Connected');

        // 2. Subscribe to the User's Private Notification Queue
        globalStompClient.subscribe('/user/queue/notifications', function (result) {
            var notification = JSON.parse(result.body);
            showGlobalToast(notification);
            updateGlobalBadge();
        });
    });
}

function showGlobalToast(notification) {
    // 1. Set Content
    document.getElementById('global-toast-body').innerText =
        notification.senderUsername + ": " + notification.content;

    // 2. Set Link (Optional: Click toast to go to chat)
    var toastLink = document.getElementById('global-toast-link');
    if(toastLink) toastLink.href = "/chats/" + notification.conversationId;

    // 3. Show Bootstrap Toast
    var toastEl = document.getElementById('liveToast');
    var toast = new bootstrap.Toast(toastEl);
    toast.show();
}

function updateGlobalBadge() {
    // Find the badge in the navbar (if it exists)
    var badge = document.getElementById("nav-notification-badge");
    if (badge) {
        var currentCount = parseInt(badge.innerText) || 0;
        badge.innerText = currentCount + 1;
        badge.style.display = 'inline-block';
    }
}
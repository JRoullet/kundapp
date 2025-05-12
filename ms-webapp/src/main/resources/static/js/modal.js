document.addEventListener('DOMContentLoaded', function () {
    const shouldOpen = [[${openModal} ?: false]];
    console.log(">> openModal =", shouldOpen); // 💡 To check in navigator
    if (shouldOpen) {
        const modalElement = document.getElementById('editNoteModal');
        const modalInstance = new mdb.Modal(modalElement);
        modalInstance.show();
    }
});
/**
 *
 * @author Loli
 */

function create() {
    var format = document.getElementById("format").value;
    var name = document.getElementById("name").value;
    $.getJSON("/CreateFile?format=" + format + "&name=" + name, createFeedback);
}

function createFeedback(data) {        
    if (data.Status === "success") {
        alert("建立成功");
    } else {
        alert("建立失敗");
    }
    window.location.reload();
}

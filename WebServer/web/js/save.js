/**
 *
 * @author Loli
 */

function save() {    
//    var code = document.getElementById("code").value;
    var code = $('#code').val();    
    $.getJSON("/Save?file=" + getParameter("file") + "&code=" + encodeURIComponent(code), saveFeedback);
}

function saveFeedback(data) {
    if(data.Status === "success"){
        alert("儲存成功");
    }else{
        alert("儲存失敗");
    }    
    window.location.reload();
}

/**
 *
 * @author Loli
 */

function getFileData() {
    $.getJSON("/GetFileData?file=" + getParameter("file"), getFileDataFeedback);
}

function getFileDataFeedback(data) {
    for (i = 0; i < data.length; i++) {
        $('.code').append(data[i] + "\n");
    }    
}

/**
 *
 * @author Loli
 */

function getFiles() {
    $.getJSON("/GetFiles", getFilesFeedback);
}

function getFilesFeedback(data) {
    for (i = 0; i < data.length; i++) {
        $('.file_list').append("<tr><td><a href=\"/edit_file.jsp?file=" + data[i] + "\">" + data[i] + "</a></td></tr>");
    }
}

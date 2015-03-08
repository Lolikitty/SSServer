/**
 *
 * @author Loli
 */

function run() {
    $.getJSON("/Run?file=" + getParameter("file"), runFeedback);
}

function runFeedback(data) {
    if (data.Error.length !== 0) {
        for (i = 0; i < data.Error.length; i++) {
            $('.output').append(data.Error[i] + "\n");
        }
    } else {
        for (i = 0; i < data.Output.length; i++) {
            $('.output').append(data.Output[i] + "\n");
        }
    }
}

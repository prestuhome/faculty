function deleteObject(url, redirectUrl) {
    $.ajax({
        url: url,
        method: "DELETE",
        success: function() {
            window.location = redirectUrl;
        },
        error: function() {
            alert("Ошибка");
        }
    })
}

function sendPutRequest(url, redirectUrl) {
    $.ajax({
        url: url,
        method: "PUT",
        success: function() {
            window.location = redirectUrl;
        },
        error: function() {
            alert("Ошибка");
        }
    })
}

function sendFormPutRequest(url, redirectUrl) {
    var form = $('#'+url);
    $.ajax({
        url: form.attr('action'),
        method: "PUT",
        data: form.serialize(),
        success: function () {
            window.location =  redirectUrl;
        },
        error: function() {
            alert("Ошибка");
        }
    })
}

function sendPostRequest(url, redirectUrl) {
    var form = $('#'+url);
    $.ajax({
        url: form.attr('action'),
        method: "POST",
        data: form.serialize(),
        success: function () {
            window.location =  redirectUrl;
        },
        error: function() {
            alert("Ошибка");
        }
    })
}

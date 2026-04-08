$(document).ready(function (){
    // Buttons
    let lengthButton = $("#length-button");
    let weightButton = $("#weight-button");
    let temperatureButton = $("#temperature-button");

    // Forms
    let lengthForm = $("#length-form");
    let weightForm = $("#weight-form");
    let temperatureForm = $("#temperature-form");

    let resultForm = $("#resultForm");

    lengthButton.click(function () {
        lengthForm.show();
        weightForm.hide();
        temperatureForm.hide()

        lengthButton.addClass("active")
        weightButton.removeClass("active")
        temperatureButton.removeClass("active")
    })
    weightButton.click(function () {
        lengthForm.hide();
        weightForm.show();
        temperatureForm.hide();

        lengthButton.removeClass("active")
        weightButton.addClass("active")
        temperatureButton.removeClass("active")
    })
    temperatureButton.click(function () {
        lengthForm.hide();
        weightForm.hide();
        temperatureForm.show();

        lengthButton.removeClass("active")
        weightButton.removeClass("active")
        temperatureButton.addClass("active")
    })

    lengthForm.submit(function (event){
        event.preventDefault();

        let data = {
            from: $("#length-from").val(),
            to: $("#length-to").val(),
            value: $("#length-value").val()
        };

        $.ajax({
            url: "/convert/length",
            type: "POST",
            data: JSON.stringify(data),
            contentType: "application/json"
        }).done(function (response) {
            resultForm.show();
            resultForm.html("Result: " + JSON.stringify(response));
        }).fail(function (response) {
            resultForm.show();
            resultForm.html("Error");
        });
    });

    weightForm.submit(function (event){
        event.preventDefault();

        let data = {
            from: $("#weight-from").val(),
            to: $("#weight-to").val(),
            value: $("#weight-value").val()
        };

        $.ajax({
            url: "/convert/weight",
            type: "POST",
            data: JSON.stringify(data),
            contentType: "application/json"
        }).done(function (response) {
            resultForm.show();
            resultForm.html("Result: " + JSON.stringify(response));
        }).fail(function (response) {
            resultForm.show();
            resultForm.html("Error");
        });
    })

    temperatureForm.submit(function (event){
        event.preventDefault();

        let data = {
            from: $("#temperature-from").val(),
            to: $("#temperature-to").val(),
            value: $("#temperature-value").val()
        };

        $.ajax({
            url: "/convert/temperature",
            type: "POST",
            data: JSON.stringify(data),
            contentType: "application/json"
        }).done(function (response) {
            resultForm.show();
            resultForm.html("Result: " + JSON.stringify(response));
        }).fail(function (response) {
            resultForm.show();
            resultForm.html("Error");
        });
    })
})
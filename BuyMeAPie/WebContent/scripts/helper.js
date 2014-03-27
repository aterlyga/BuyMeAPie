// Updates the view based on the current content of the model
function refreshView() {
    // Make the proper sorting of the model
	sortItemsToBuy();
    
	$("#table_of_items_to_buy tbody").fadeOut(100, function() {
		// Remove all lines in the table
		$("#table_of_items_to_buy tbody tr").remove();		
		// Draw the new lines, based on the model
		$.each(itemsToBuy, function(index, object) {
			if (object.purchased == 0) {
				$("#table_of_items_to_buy tbody").append("<tr></tr>").fadeIn(200);
			} else {
				$("#table_of_items_to_buy tbody").append("<tr class=\"purchased\">", "</tr>").fadeIn(200);
			};
			$("#table_of_items_to_buy tr:last").append("<td>" + "<img alt=\"Edit\" src=\"images/edit.jpg\">" + "</td>");
			$("#table_of_items_to_buy tr:last").append("<td style = \"display:none\">" + object.id + "</td>");
			$("#table_of_items_to_buy tr:last").append("<td>" + object.name + "</td>");
			$("#table_of_items_to_buy tr:last").append("<td>" + object.amount + "</td>");
			$("#table_of_items_to_buy tr:last").append("<td style = \"display:none\">" + object.purchased + "</td>");
		});
    });
	
	// Re-initialize the new item input widgets
	$("#search").val("").focus();
	$("#amount").val("");
	$("#button").attr("onclick", "addItemToBuy()");
};


// Ajax request to execute a server action
function requestToServer(url, jsonObject, callbackFunction) {
    $.ajax({
		type: "POST",
		url: url,
		cache: false,
		data: {items_to_buy: JSON.stringify(jsonObject)},
		success: function(response) {
			hideProgressMessage();
			parsedResponse = $.parseJSON(response);
			if (parsedResponse.error) {
				showError(parsedResponse.errorCode);
			} else {
				callbackFunction(parsedResponse);
			}
		},
		error: function(error) {
			alert(error);
		}
    });
	showProgressMessage();
}

// Shows the progress message
var showProgressMessage = function() {
    if ($("#list_of_items_to_buy").find("p").length == 0) {
		var text = "Working...";
		$("#list_of_items_to_buy").append("<p>" + text + "</p>");
    }	
}

// Hides the progress message
var hideProgressMessage = function() {
	$("#list_of_items_to_buy p").remove();
}

// Displays an error message
function showError(errorCode) {
    switch (errorCode) {
		case 1:
			var errorMessage = "Internal server error\nNo data has been changed" +
				"\nPlease, contact your administrator";
			alert(errorMessage);
			break;
		case 2:
			var errorMessage = "Incorect data\nPlease check and try again";
			alert(errorMessage);
			break;
    }
}

// Validate input field (name)
function validateItemToBuy() {
    if ($.trim($("#search").val()) == "") {
		for (var i = 0; i <= 2; i++) {
			$("#search").effect("highlight", {color: "#ff0000"}, 1000).focus();
		};
		return false;
    };
}

// Sorting of the model
function sortItemsToBuy () {
    if (itemsToBuy.length != 0) {
        itemsToBuy.sort(function(a, b) {
    		return (a.purchased < b.purchased ? -1 : a.purchased > b.purchased ? 1 : a.name < b.name ? -1 : a.name > b.name ? 1 : 0);
        });
    }
}
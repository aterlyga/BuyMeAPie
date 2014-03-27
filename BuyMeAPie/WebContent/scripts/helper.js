function refreshView() {
    sortItemsToBuy ();
    $("#table_of_items_to_buy tbody").fadeOut(100, function() {
	$("#table_of_items_to_buy tbody tr").remove();
    
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


// Ajax request to load list of items to buy with ability to specify URL and DATA
function requestToServer(url, json_object, func) {
    $.ajax( {
	type: "POST",
	url: url,
	cache: false,
	data: {items_to_buy: JSON.stringify(json_object)},
	success: function(response) {
	    responseOfItemToBuy = $.parseJSON(response);
	    if (responseOfItemToBuy.error === "true") {
		serverAlert(responseOfItemToBuy.errorCode);
		$("#list_of_items_to_buy p").remove();
	    } else {
		$("#list_of_items_to_buy p").remove();
		func(responseOfItemToBuy);
	    }
	},
	error: function(error) {
	    alert (error);
	}
    });
    if ($("#list_of_items_to_buy").find("p").length == 0) {
	var text = "Adding ...";
	$("#list_of_items_to_buy").append("<p>" + text + "</p>");
    }
}

function serverAlert(errorCode) {
    switch (errorCode) {
    case 1:
	var errorMessage = "Some error in server occured.\nData hasn't been changed." +
			"\nPlease, tell an administrator about it.";
	alert(errorMessage);
	break;
    case 2:
	var errorMessage = "Incorect data was sended.\nCheck it, please";
	alert(errorMessage);
	break;
    }
}

// Validate input field (name)
function validateItemToBuy() {
    if ($.trim($("#search").val()) == "") {
	for(var i = 0; i <= 2; i++) {
	    $("#search").effect("highlight", {color: "#ff0000"}, 1000).focus();
	};
	return false;
    };
}


// Sorting Modal
function sortItemsToBuy () {
    if (itemsToBuy.length != 0) {
        itemsToBuy.sort(function(a, b) {
    		return (a.purchased < b.purchased ? -1 : a.purchased > b.purchased ? 1 : a.name < b.name ? -1 : a.name > b.name ? 1 : 0);
        });
    }
}

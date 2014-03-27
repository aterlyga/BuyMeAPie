// This variable stores the model part of the MVP
var itemsToBuy = [];

// Global variables
var currentRowSelector = null;
	    
// Initializations for the first page load
$(document).ready(function() {
    
	// Add jQuery "autocomplete" feature to "search" textbox
    $("#search").autocomplete({
		source: [], 
		autoFocus: true
		}); 
    
	// Handle the "Key Up" event to appropriately narrow
	// the displayed list of products 
    $("#search").keyup(function () { 
			
			// Retrieve entered text
			searchValue = $(this).val();
			
			// Request matching products list from server
			if (searchValue != "") {
				searchItem = {"name": searchValue};
				
				// Ajax success callback
				var autocompleteCallback = function(responseData) {
					// Initialize the products list variable
					var productsList = [];
					
					// Parse the received response
					$.each(responseData, function(index, object) {
						productsList.push(object.name);
					});
					
					// Re-initialize the auto-complete products list
					$("#search").autocomplete(
						"option", 
						"source", 
						productsList
						);
				};	    
				
				requestToServer(
					"item_auto_complete",
					searchItem, 
					autocompleteCallback
					);
			}
		});
	
	// Ajax success callback
	var firstPageLoadCallback = function(listOfItemsToBuy) {
		itemsToBuy = listOfItemsToBuy;
		refreshView();
	};

	// Initialization of the items to buy table
	requestToServer(
		"get_items_to_buy", 
		null, 
		firstPageLoadCallback
		);
});

// Adding new item to buy with validating data 
// and displaying new row in list 
function addItemToBuy() {
    if (validateItemToBuy()) {
		// Initialize new ItemToBuy object
		newItemToBuy = [];
    	newItemToBuy.push({
    	    "name": $("#search").val(),
    	    "amount": $("#amount").val()
    	});
  
		// Ajax success callback
		var addItemToBuyCallback = function(addedItemToBuy) {
			// Update model using returned objects
			$.each(addedItemToBuy, function(index, object) {
				itemsToBuy.push(object);
			});
			refreshView();
		};
  
		// Submit the new object to server
    	requestToServer(
			"action/add_new_item_to_buy", 
			newItemToBuy, 
			addItemToBuyCallback
			);
    	
		// Clear controls
		$("#search").val("").focus();
    	$("#amount").val("");
    }
};
    
// Edit item in list
$(document).ready(function() {
    $("#table_of_items_to_buy").on("click", "img", function() {
	currentRowSelector = "#table_of_items_to_buy tbody tr:eq(" + $(this).parents("tr:first").index() + ")";
	    
	$("#search").val($(currentRowSelector).find("td").eq(2).text());
	$("#amount").val($(currentRowSelector).find("td").eq(3).text());
	$("#button").attr("onclick", "editItemToBuy()");
    });
});
	
function editItemToBuy() {
    if (validateItemToBuy() != false) {
	editedItemToBuy = [];
	editedItemToBuy.push({
	    "id": $(currentRowSelector).find("td").eq(1).text(),
	    "name": $("#search").val(),
	    "amount": $("#amount").val()
	});
    };

    requestToServer("action/edit_item_to_buy", editedItemToBuy, updateView);
    $("#search").val("").focus();
    $("#amount").val("");
    $("#button").attr("onclick", "addItemToBuy()");
};

var updateView = function(editedItemToBuy) {
    $.each(editedItemToBuy, function(index, object) {
	$.each(itemsToBuy, function(index, element) {
	    if (element.id == object.id) {
		element.name = object.name;
		element.amount = object.amount;
	    };
	});
    });
    refreshView();
};
	

// Purchased/Not purchased item
$(document).ready(function() {
    $("#table_of_items_to_buy tbody").on("click", "tr", function(event) {
	if (!$(event.target).is("img")) {
	    purchaseItemToBuy = [];
	    purchaseItemToBuy.push({
		"id": $(this).find("td").eq(1).text(),
		"purchased": $(this).find("td").eq(4).text()
	    });
	    
	    requestToServer("action/purchase_item_to_buy", purchaseItemToBuy, purchaseItem);
	}
    });
});

var purchaseItem = function(purchasedItem) {
    $.each(purchasedItem, function(index, object) {
	$.each(itemsToBuy, function(index, element) {
	    if (element.id == object.id) {
		element.purchased = object.purchased;
	    };
	});
    });
    refreshView();
};
    
    

// Clear whole list of items to buy
$(document).ready(function() {
    $("#confirm_truncate").dialog({
        autoOpen: false,
        resizable: false,
        modal: true,
        buttons: {
        	"Delete": function() {
        	    requestToServer("truncate_table", null, pageReload);
        	    $(this).dialog("close");
        	},
        	"Cancel": function() {
        	    $(this).dialog("close");
        	}
        },
        closeOnEscape: true,
        show: {
        	effect: "bounce",
        	distance: 3,
        	times: 3
        }
    });
    $("#truncate").on("click", function() {
	$("#confirm_truncate").dialog("open");
    });
});

var pageReload = function(emptyItemsToBuy) {
    itemsToBuy = emptyItemsToBuy;
    refreshView();
};

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

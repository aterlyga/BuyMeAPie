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
    
	// Add "Key Up" event handler in order to appropriately narrow
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
	
	// Initialization of the items to buy table
	var firstPageLoadCallback = function(listOfItemsToBuy) {
		itemsToBuy = listOfItemsToBuy;
		refreshView();
	};
	requestToServer(
		"get_items_to_buy", 
		null, 
		firstPageLoadCallback
		);

	// Add event handler for "Edit" buttons in the grid
    $("#table_of_items_to_buy").on(
		"click", 
		"img", 
		editItemToBuy
		);

	// Add event handler for purchasing item when user clicks 
	// a rw in the table
    $("#table_of_items_to_buy tbody").on(
		"click", 
		"tr", 
		function(event) {
			if (!$(event.target).is("img")) {
				purchasedItemToBuy = [];
				purchasedItemToBuy.push({
					"id": $(this).find("td").eq(1).text(),
					"purchased": $(this).find("td").eq(4).text()
				});		
				purchaseItemToBuy(purchasedItemToBuy);
			}
		}
	);
	
	// Confirmation dialog about whole list clearing
	$("#confirm_truncate").dialog({
        autoOpen: false,
        resizable: false,
        modal: true,
        buttons: {
        	"Delete": function() {
				clearItemsToBuy();
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
    }
};
    
// Start editing an item to buy
// TODO: Need to pass the ID of the item to buy, which 
// is retrieved using "currentRowSelector" inside of the event handler.
// This ID will be used inside of "editItemToBuy" in order to find the name 
// and amount in the model. This is for cleaner and more understandable code.
function editItemToBuy() {
	// Initialize the row selector
	currentRowSelector = "#table_of_items_to_buy tbody tr:eq(" + $(this).parents("tr:first").index() + ")";
		
	// Copy values from the table row to widgets
	$("#search").val($(currentRowSelector).find("td").eq(2).text());
	$("#amount").val($(currentRowSelector).find("td").eq(3).text());
	
	// TODO: Set the edited item ID in the global variable or hidden input
	// in order to reuse it in the "saveItemToBuy"
	
	// Initialize the "Save" button handler
	$("#button").attr("onclick", "saveItemToBuy()");
}
	
// Save modified item to buy
function saveItemToBuy() {
    if (validateItemToBuy()) {
		// Initialize ItemToBuy object
		editedItemToBuy = [];
		editedItemToBuy.push({
			"id": $(currentRowSelector).find("td").eq(1).text(),  // TODO: use the reviously stored ID (see function above)
			"name": $("#search").val(),
			"amount": $("#amount").val()
		});
	
		// Ajax success callback
		var saveItemToBuyCallback = function(editedItemToBuy) {
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
	
		// Submit the modified object to server
	    requestToServer(
			"action/edit_item_to_buy", 
			editedItemToBuy, 
			saveItemToBuyCallback
			);			
    };
};	

var purchaseItemToBuy = function(itemToBuy) {
	// Ajax success callback
	var purchaseItemToBuyCallback = function(purchasedItem) {
		$.each(purchasedItem, function(index, object) {
			$.each(itemsToBuy, function(index, element) {
				if (element.id == object.id) {
					element.purchased = object.purchased;
				};
			});
		});
		refreshView();
	};
	
	// Submit an object to server
	requestToServer(
		"action/purchase_item_to_buy", 
		purchasedItem, 
		purchaseItemToBuyCallback
		);
}       

// Clear whole list of items to buy
var clearItemsToBuy = function() {
	// Ajax success callback
	var clearItemsToBuyCallback = function() {
		itemsToBuy = [];
		refreshView();
	};
	
	// Submit an object to server
	requestToServer(
		"truncate_table", 
		null, 
		clearItemsToBuyCallback
		);
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

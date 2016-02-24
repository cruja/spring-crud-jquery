$(document).ready(function() {
	
	jQuery.fn.extend({
		updateEntity : function(id) {
			// populate template; for new entry received id is 0.
			url = (id != 0 ? "/" + id : "");
			$.ajax({url: "/users" + url, accepts:"text/plain, charset=utf-8", dataType: "text/plain", success: function(result){
					$("#dialog-form").html(result);
			}});
			$('#dialog-form').dialog('open');
		 }
	});

	var table = $('table#users').DataTable({
		'ajax' : '/data/users',
		'serverSide' : true,
		"search": {
		    "caseSensitive": false
		 },
		columns : [ {
			data : 'id'
		}, {
			data : 'name'
		}, {
			data : 'email'
		}, {
			data : 'role'
		}, {
			data : 'status'
				
			// columns not persisted on the server-side			
		}, {			
			data : 'edit',
			orderable : false,
			searchable : false,
			render : function(data, type, row) {				
				return "<img src='/images/update.png' alt='update' class='icon' onclick=\"$(this).updateEntity(" + row.id + ")\"/>";  
			}
		}, {
			data : 'deactivate',
			orderable : false,
			searchable : false,
			render : function(data, type, row) {
				if (row.status == "ACTIVE") {
					return "<a href='/users/" + row.id +"/block'>" 
					+ " <img src='/images/delete.png' alt='block' class='icon' /></a>";
				} else {
					return "<a href='/users/" + row.id +"/activate'>" 
					+ " <img src='/images/upload.jpg' alt='activate' class='icon' /></a>";	
				}
			}
		}
		]
	});
	
	// load create-users template
	$.ajax({url: "/users", success: function(result){
	        $("#create-user").html(result);
    }});	
});
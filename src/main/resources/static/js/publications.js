$(document).ready(function() {
	
	jQuery.fn.extend({
		updateEntity : function(id) {
			// populate template; for new entry received id is 0.
			url = (id != 0 ? "/" + id : "");
			$.ajax({url: "/publications" + url, success: function(result){
					$("#dialog-form").html(result);
			}});
			$('#dialog-form').dialog('open');
		 }
	});

	var table = $('table#publications').DataTable({
		'ajax' : '/data/publications',
		'serverSide' : true,
		"search": {
		    "caseSensitive": false
		 },
		columns : [ {
			data : 'title'
		}, {
			data : 'author'
		}, {
			data : 'year'
		}, {

			// columns not persisted on the server-side			
			data : 'publisher',
			searchable : false,
			render : function(data, type, row) {
				if (row.publisher) {
					return row.publisher.name;
				}
				return '';
			}
		}, {			
			data : 'edit',
			orderable : false,
			searchable : false,
			render : function(data, type, row) {				
				return "<img src='/images/update.png' alt='update' class='icon' onclick=\"$(this).updateEntity(" + row.id + ")\"/>";  
			}
		}, {
			data : 'delete',
			orderable : false,
			searchable : false,
			render : function(data, type, row) {
				return "<form method='DELETE' action=/publications/" + row.id +"/delete >"
				+	" <input type='image' src='/images/delete.png' alt='delete' class='icon' /> </form>"; 
			}
		}
		]
	});
	
	// load create-publications template
	$.ajax({url: "/publications", success: function(result){
	        $("#create-publication").html(result);
    }});	
});
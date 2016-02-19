$(document).ready(function() {
	
	var table = $('table#subscriptions').DataTable({
		'ajax' : '/data/usersubscriptions',
		'serverSide' : true,
		"search": {
		    "caseSensitive": false
		 },
		columns : [ {
			data : 'id'
		}, {
			data : 'type'
		}, {
			data : 'date'
		}, {

			// columns not persisted on the server-side			
			data : 'user',
			searchable : false,
			render : function(data, type, row) {
				if (row.user) {
					return row.user.email;
				}
				return '';
			}
		}, {			
			data : 'publication',
			searchable : false,
			render : function(data, type, row) {				
				if (row.publication) {
					return row.publication.title;
				}
				return '';
			}
		}
		]
	});
	
});
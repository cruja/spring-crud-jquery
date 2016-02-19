$(document).ready(function() {
	var table = $('table#pubsubscriptions').DataTable({
		'ajax' : '/data/pubsubscriptions',
		'serverSide' : true,
		"search": {
		    "caseSensitive": false
		 },
		columns : [ {
			data : 'title',
			render : function(data, type, row) {
				return row.publication.title;
			}
		}, {
			data : 'author',
			render : function(data, type, row) {
				return row.publication.author;
			}

		}, {
			data : 'year',
			render : function(data, type, row) {
				return row.publication.year;
			}
		}, {
			// columns not persisted on the server-side
			data : 'publisher',
			searchable : false,
			render : function(data, type, row) {
				return row.publication.publisher.name;
			}
		}, {
			data : 'subscription',
			orderable : false,
			searchable : false,
			render : function(data, type, row) {
				if (row.subscription != null) {
					return "<a href='/pubsubscriptions/" + row.subscription.id +"/unsubscribe'>" 
					+ " <img src='/images/delete.png' alt='delete' class='icon' /></a>";
				} else {
					return "<a href='/pubsubscriptions/" + row.publication.id +"/subscribe'>" 
					+ " <img src='/images/upload.jpg' alt='delete' class='icon' /></a>";	
				}
			}
		}, {
			data : 'type',
			orderable : false,
			searchable : false,
			render : function(data, type, row) {
				return (row.subscription != null) ? row.subscription.type : "";
			}

		}
		
		]
	});
});
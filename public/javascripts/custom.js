$(function() {
	$( "#tabs" ).tabs();
});

var openModal = function (modal_obj) {
	modal_url = modal_obj.id;
	$("<div id='modalDlg'>").dialog({
		modal: true,
		open: function () {
			$(this).load(modal_url);
		}, 
		close: function () {
			$(this).dialog('destroy').remove();
		},
		width: 800,
		height: 500,
		title: ''
	});
}

var getIcon = function(icon_name, icon_title) {
	return "<ul id='icons' class='ui-widget ui-helper-clearfix'><li class='ui-state-default ui-corner-all' title='" + 
		icon_title + "'><span class='ui-icon ui-icon-" + icon_name + "'></span></li></ul>";
}
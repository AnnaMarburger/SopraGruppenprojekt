extends WindowDialog

var width;
var height;

# Called when the node enters the scene tree for the first time.
func _ready():
	pass # Replace with function body.

#save input when OK Button is pressed
func _on_OKButton_pressed():
	var width_tmp = find_node("Width").text.strip_edges()
	var height_tmp = find_node("Height").text.strip_edges()

	
	if typeof(width_tmp) == TYPE_INT && typeof(height) == TYPE_INT:
		width = width_tmp
		height = height_tmp
	#TODO: add else for invalid input
	get_tree().change_scene("res://Scenes/newConfigScene/newEditorScreen.tscn")
	hide()
	pass # Replace with function body.


func _on_newConfigButton_pressed():
	popup_centered()
	pass # Replace with function body.

extends AudioStreamPlayer2D


var audio = preload("res://Soundtrack.mp3")


func _ready():
	stream= audio;
	play();




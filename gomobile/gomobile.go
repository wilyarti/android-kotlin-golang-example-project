package gomobile

import (
	"fmt"
	"time"
)

// callback
var jc JavaCallback

type JavaCallback interface {
	SendString(string)
}

func RegisterJavaCallback(c JavaCallback) {
	jc = c
}

func TestCall() {
	for i := 0; i < 100; i++ {
		time.Sleep(100 * time.Millisecond)
		jc.SendString(fmt.Sprintln("Counting... ", i))
	}
}

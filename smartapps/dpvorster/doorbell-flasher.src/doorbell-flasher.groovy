/**
 *  Doorbell
 *
 *  Copyright 2014 Daniel Vorster
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Doorbell Flasher",
    namespace: "dpvorster",
    author: "Daniel Vorster",
    description: "Detects a doorbell and flashes a light",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {

	section("When the doorbell rings...") {
		input "contact", "capability.button", title: "Button?"
	}
    section("Then flash ...") {
		input "switch1", "capability.switch", title: "This light?"	
        input "flashCount", "number", title: "So many times", defaultValue: 3
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
    
    state.lastActivated = 0L
    subscribe (contact, "button", "eventHandler")
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
    state.lastActivated = 0L
    subscribe (contact, "button", "eventHandler")
}
     
def eventHandler(evt) {

	log.debug "Event is $evt.value"
	if (evt.value != "pushed") {
    	return
    }

	if (state.lastActivated) {
    
    	if (now() - state.lastActivated < 8000) {
        	return
        }
    }
    
    log.debug "Flashing light"
    
    // Now using device type flash command
    switch1.alert(flashCount)
    
    /*
    state.lastActivated = now();
    
    def first = (switch1.currentSwitch == "on" ? "off" : "on");
    def second = (first == "off" ? "on" : "off");
    	
    toggleSwitch (switch1, first,  0)
    toggleSwitch (switch1, second, 1000)
    toggleSwitch (switch1, first,  2000)
    toggleSwitch (switch1, second, 3000)
    toggleSwitch (switch1, first,  4000)
    toggleSwitch (switch1, second, 5000)
    // Repeat to make sure the light ends in the right state
    toggleSwitch (switch1, second, 6000)
	*/
}

def toggleSwitch(s, cmd, waitFor) {

	if (cmd == "on") {
    	s.on (delay: waitFor)
    }
    else {
    	s.off (delay: waitFor)
    }
}
    	


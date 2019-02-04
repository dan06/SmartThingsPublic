/**
 *  Thermostat Nanny
 *
 *  Author: dpvorster
 *  Date: 2015-12-05
 */
definition(
    name: "Thermostat Nanny",
    namespace: "dpvorster",
    author: "dpvorster",
    description: "Checks that the thermostat settings don't change",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo@2x.png"
)

preferences 
{	
	section("Thermostats?") {
		input "thermostats", "capability.thermostat", title: "Which?", required: true, multiple: true
	}
	section("Setpoints"){
			input "heatingSetpoint", "number", title: "Heating?", required: true
            input "coolingSetpoint", "number", title: "Cooling?", required: true
	}
	/*section ("") {
		//label title: "Assign a name", required: false
		//input "modes", "mode", title: "Set for specific mode(s)", multiple: true, required: false
	}*/
} 

def installed() 
{
	initialize()
}

def updated() 
{
	initialize()
}

def initialize()
{   
	unsubscribe()
	subscribe (thermostats, "heatingSetpoint", eventHandler)
	subscribe (thermostats, "coolingSetpoint", eventHandler)
	subscribe (thermostats, "temperature", eventHandler)
    log.debug "Subscribed to $thermostats"
}

def eventHandler(evt)
{
	log.debug "Got event $evt.name, $evt.value"
	if (!correctMode())
    {
    	log.debug "eventHandler, not in the correct mode"
    	return
    }

	// Check each thermostat
	for(thermostat in thermostats)
	{
        def heatSet = thermostat.currentValue("heatingSetpoint")
        def coolSet = thermostat.currentValue("coolingSetpoint")
    	log.debug "Checking $thermostat ($heatSet, $coolSet)"
        
        // Allow settings to be lower but not higher
		if (heatSet > heatingSetpoint || coolSet > coolingSetpoint)
		{
			sendSetpoints(thermostat)
		}
	}
} 

def sendSetpoints(thermostat)
{
	thermostat.setCoolingSetpoint(coolingSetpoint);
	thermostat.setHeatingSetpoint(heatingSetpoint);
	sendNotificationEvent("$thermostat set to $coolingSetpoint, $heatingSetpoint")
    log.debug "$thermostat set to $coolingSetpoint, $heatingSetpoint"
}

private correctMode()
{
	def result = !modes || modes.contains(location.mode)
	log.debug "correctMode = $result"
	result
}
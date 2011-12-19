/* 
/  Copyright (C) 2009  Antti Alho - Sesca Innovations Ltd
/  
/  This file is part of SIP-Applet (www.sesca.com, www.purplescout.com)
/
/  This program is free software; you can redistribute it and/or
/  modify it under the terms of the GNU General Public License
/  as published by the Free Software Foundation; either version 2
/  of the License, or (at your option) any later version.
/
/  This program is distributed in the hope that it will be useful,
/  but WITHOUT ANY WARRANTY; without even the implied warranty of
/  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
/  GNU General Public License for more details.
/
/  You should have received a copy of the GNU General Public License
/  along with this program; if not, write to the Free Software
/  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/


/**
 * 
 * @author Antti Alho
 * @class AudioSettings
 * 
 * Finds MICROPHONE port (recording device)
 * and tries to make sure that it is working properly
 *
 */

package com.sesca.voip.media;

import javax.sound.sampled.*;

public class AudioSettings
{
	public String os = System.getProperty("os.name").toUpperCase();
	
	private boolean hasMuteControl 	= false;
	private boolean hasSelectControl 	= false;
	private boolean hasBoostControl 	= false;
	private boolean hasVolumeControl 	= false;
	
	private static String RECORD_PORT_SELECT = "MICROPHONE";
	private FloatControl volumeControl;
	private BooleanControl selectControl;
	private BooleanControl muteControl;
	private BooleanControl boostControl;
	private TargetDataLine recordLine = null;
	
	Port recorderPort;
	
	public AudioSettings() throws LineUnavailableException
	{
		init();
	}

	private void init() throws LineUnavailableException
	{
		//AudioFormat recordingFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
		AudioFormat recordingFormat = new AudioFormat(8000.0F, 16, 1, true, false);
		
		recordLine = (TargetDataLine) AudioSystem.getTargetDataLine(recordingFormat);

		recordLine.open(recordingFormat);
		adjustAudioSettings();
		recorderPort.open();
		getRecorderControls();
		recordLine.close();
	}

	private void adjustAudioSettings() throws LineUnavailableException
	{
		Port.Info recPortInfo = new Port.Info(Port.class, RECORD_PORT_SELECT, true);
		recorderPort = (Port) AudioSystem.getLine(recPortInfo);
		recorderPort.open();//getRecorderControllers();
		getRecorderControls();
		recorderPort.close();
		
		if(muteControl != null)
			hasMuteControl = true;
		if(boostControl != null)
			hasBoostControl = true;
		if(selectControl != null)
			hasSelectControl = true;
		if(volumeControl != null)
			hasVolumeControl = true;

	}

	private void getRecorderControls() throws LineUnavailableException
	{
		Control[] controls = recorderPort.getControls();
		for (int i = 0; i < controls.length; i++)
		{
			if(controls[i] instanceof CompoundControl)
			{
				Control[] members = ((CompoundControl) controls[i]).getMemberControls();
				for (int j = 0; j < members.length; j++)
				{
					setController(members[j]);
				} 
			} 
			else
				setController(controls[i]);
		} 
	}
	
	private void setController(Control control)
	{
		if(control.getType().toString().equals("Select"))
		{
			selectControl = (BooleanControl) control;
			
		}
		else if(control.getType().toString().equals("Volume"))
		{
			volumeControl = (FloatControl) control;
			
		}
		else if(control.getType().toString().equals("Mute"))
		{
			muteControl = (BooleanControl) control;
			
			
		}
		else if(control.getType().toString().equals("Microphone Boost"))
		{
			boostControl = (BooleanControl) control;
			
		}
		else
		{
			// DO NOTHING... we don't need other controls
		}

	}
	
	public float getRecordingVolume()
	{
		return volumeControl.getValue();
	}

	public boolean getMuteValue()
	{
		return muteControl.getValue();
	}

	public boolean getBoostValue()
	{
		return boostControl.getValue();
	}

	public boolean getSelectValue()
	{
		return selectControl.getValue();
	}

	public void setRecordingVolume()
	{
	// do nothing
	}

	public void setMuteValue(boolean b) throws LineUnavailableException
	{
		recorderPort.open();
		getRecorderControls();
		muteControl.setValue(b);
		recorderPort.close();
	}

	public void setBoostValue(boolean b) throws LineUnavailableException
	{
		recorderPort.open();
		getRecorderControls();
		boostControl.setValue(b);
		recorderPort.close();
	}

	public void setSelectValue(boolean b) throws LineUnavailableException
	{
		recorderPort.open();
		getRecorderControls();
		selectControl.setValue(true);
		recorderPort.close();
	}
	
	public void setVolumeValue(float f) throws LineUnavailableException
	{
		recorderPort.open();
		getRecorderControls();
		volumeControl.setValue(f);
		recorderPort.close();
	}

	public boolean isHasMuteControl()
	{
		return hasMuteControl;
	}

	public boolean isHasSelectControl()
	{
		return hasSelectControl;
	}

	public boolean isHasBoostControl()
	{
		return hasBoostControl;
	}

	public boolean isHasVolumeControl()
	{
		return hasVolumeControl;
	}
}

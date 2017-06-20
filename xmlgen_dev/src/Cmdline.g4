grammar Cmdline;

@header {
package org.xmlgen.parser.cmdline;

import org.xmlgen.context.Context; 
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack; 

import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.notifications.LocationImpl;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Artifact;
import org.xmlgen.notifications.ContextualNotification;

import java.io.File;
}

@parser::members {   
	final private Notification argumentValueMissing = new Notification(Module.Parameters_check, Gravity.Error, Subject.Command_Line, Message.Argument_Value_Missing);	
	final private Notification duplicateDataSourceReference = new Notification(Module.Parameters_check, Gravity.Error, Subject.DataSource, Message.Duplicate_Reference);
	
	final private Notifications notifications = Notifications.getInstance();
}

WS
:
	[ \t\r\n]+ -> skip
; // skip spaces, tabs, newlines

cmdline
:
	cmd* EOF
;

cmd
:
	dataSource
	| template
	| schema
	| output
	| trace
	| user_services
;

dataSource
:
	id = Ident '=' filename = Filename
	{
	                                           String filename = $filename.text.substring(1, $filename.text.length()-1);
	                                           FrameStack frameStack = Context.getInstance().getFrameStack();
	                                           Frame topFrame = frameStack.peek();
	                                           if ($id.type == Ident)
	                                           {
	                                           	  if (topFrame.get($id.text) != null)
	                                              {	                                              
	                                                 LocationImpl location = new LocationImpl(new Artifact($id.text), $id.index, $id.pos, $id.line);	  
	                                                 ContextualNotification contextNotification = new ContextualNotification(duplicateDataSourceReference, location);
	                                                 notifications.add(contextNotification); 
	                                              } 
	                                              else
	                                              {
	                                              	if ($filename.type == Filename)
	                                              	{
	                                              	   topFrame.put($id.text, filename);	
	                                              	}
	                                              	else
	                                              	{
	                                              		LocationImpl location = new LocationImpl(new Artifact($filename.text), $filename.index, $filename.pos, $filename.line);
	                                              		ContextualNotification contextNotification = new ContextualNotification(argumentValueMissing, location);
	                                              		notifications.add(contextNotification); 
	                                              	}
	                                              }
	                                           }
	                                           else 
	                                           {
	                                           	  LocationImpl location = new LocationImpl(new Artifact($id.text), $id.index, $id.pos, $id.line);
	                                              ContextualNotification contextNotification = new ContextualNotification(argumentValueMissing, location);
	                                              notifications.add(contextNotification); 
	                                           }
    }

;

output
:
	'--output' filename = Filename
	{
	                                           if ($filename.type == Filename)
	                                           {
	                                           	  String filename = $filename.text.substring(1, $filename.text.length()-1);
	                                           	  Context.getInstance().setOutput(filename);		                                       
		                                       }
		                                       else
		                                       {
		                                       	  LocationImpl location = new LocationImpl(new Artifact($text), $filename.index, $filename.pos, $filename.line);	  
	                                              ContextualNotification contextNotification = new ContextualNotification(argumentValueMissing, location);
	                                              notifications.add(contextNotification); 
		                                       	  Context.getInstance().setOutput(null);
		                                       }
    }

;

schema
:
	'--schema' filename = Filename
	{   
	                                           if ($filename.type == Filename)
	                                           {
	                                           	  String filename = $filename.text.substring(1, $filename.text.length()-1);
	                                           	  Context.getInstance().setSchema(filename);		                                       
		                                       }
		                                       else
		                                       {
		                                       	  LocationImpl location = new LocationImpl(new Artifact($text), $filename.index, $filename.pos, $filename.line);	  
	                                              ContextualNotification contextNotification = new ContextualNotification(argumentValueMissing, location);
	                                              notifications.add(contextNotification); 
		                                       	  Context.getInstance().setSchema(null);
		                                       }
    }

;

template
:
	'--template' filename = Filename
	{
	                                           if ($filename.type == Filename)
	                                           {
	                                           	  String filename = $filename.text.substring(1, $filename.text.length()-1);
	                                           	  Context.getInstance().setXmlTemplate(filename);		                                       
		                                       }
		                                       else
		                                       {
		                                       	  LocationImpl location = new LocationImpl(new Artifact($text), $filename.index, $filename.pos, $filename.line);	  
	                                              ContextualNotification contextNotification = new ContextualNotification(argumentValueMissing, location);
	                                              notifications.add(contextNotification); 
		                                       	  Context.getInstance().setXmlTemplate(null);
		                                       }
    }

;

trace
:
	'--trace'
	{
		                                        Context.getInstance().setTrace();
    }

;

user_services
:
	'--services' servicesList
;

servicesList
:
	ident = dottedIdent servicesList
	{
                                                Context.getInstance().registerUserService($ident.text);
    }

	|
;

dottedIdent
:
	Ident
	(
		'.' Ident
	)*
;

Filename
:
	'\''
	(
		ESC
		| .
	)*? '\''
;

fragment
ESC
:
	'\\"'
	| '\\\\'
;

// Rigourously the same as Acceleo Query Language

Ident
:
	(
		Letter
		| '_'
	)
	(
		Letter
		| [0-9]
		| '_'
	)*
;

fragment
Letter
:
	[a-zA-Z]
;


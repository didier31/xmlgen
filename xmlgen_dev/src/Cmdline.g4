grammar Cmdline;

@header 
{
package org.xmlgen.parser.cmdline;

import org.xmlgen.context.Context; 
import org.xmlgen.context.Frame;
import org.xmlgen.context.FrameStack; 

import org.xmlgen.notifications.Notification;
import org.xmlgen.notifications.Notification.Gravity;
import org.xmlgen.notifications.Notification.Message;
import org.xmlgen.notifications.Notification.Module;
import org.xmlgen.notifications.Notification.Subject;
import org.xmlgen.notifications.Notifications;
import org.xmlgen.notifications.Artefact;
import org.xmlgen.notifications.ContextualNotification;
}

@parser::members 
{ 
	final private Notification duplicateDataSourceReference = new Notification(Module.Parameters_check, Gravity.Fatal, Subject.DataSource, Message.Duplicate_Reference);
}

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines

cmdline : cmd* EOF
;

cmd : dataSource | template | schema | output
;

dataSource : id=Ident '=' filename=Filename {
	                                           String filename = $filename.text.substring(1, $filename.text.length()-1);
	                                           FrameStack frameStack = Context.getInstance().getFrameStack();
	                                           Frame topFrame = frameStack.peek();
	                                           if (topFrame.get($id.text) != null)
	                                              {
	                                              	  Notifications notifications = Notifications.getInstance(); 
	                                              	  Artefact artefact = new Artefact($id.text);	                                              	  
	                                              	  notifications.add(new ContextualNotification(duplicateDataSourceReference, artefact)); 
	                                              } 
	                                           else
	                                              {
	                                                  topFrame.put($id.text, filename);
	                                              }
                                            }
;

output : '--output' filename=Filename       {
		                                        String filename = $filename.text.substring(1, $filename.text.length()-1);
		                                        Context.getInstance().setOutput(filename);
                                            }
;

schema : '--schema' filename=Filename       {   
	                                            String filename = $filename.text.substring(1, $filename.text.length()-1);
	                                            Context.getInstance().setSchema(filename);
                                            }
;

template : '--template' filename=Filename   {
	                                           String filename = $filename.text.substring(1, $filename.text.length()-1); 
	                                           Context.getInstance().setXmlTemplate(filename);
                                            }
;

Filename : '\''  (ESC|.)*?  '\''
;

fragment ESC : '\\"' | '\\\\'
;

// Rigourously the same as Acceleo Query Language

Ident : (Letter | '_') (Letter | [0-9] | '_')*
;
fragment Letter : [a-zA-Z]
;


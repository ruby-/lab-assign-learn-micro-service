from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

import smtplib
from email.mime.text import MIMEText
from email.header import Header

from msg.service.api import MSGService

# Email sender setup : we use gmail smtp service for this demo purpose
gmail_user = 'leetcode.team.stars@gmail.com'
gmail_password = 'XiaoleiandLi513'

from_address = gmail_user

class MSGServiceHandler:
    def sendMobileMSG(self, mobile, msg):
        # Requires mobile service carrier's service, fake the service here by simply printing out a message
        print("Send message to phone number:" + mobile + ", the msg: " + msg)
        return True


def sendEmailMessage(self, email, msg):
    print("Send message to email address: " + email + ", the msg: " + msg)
    msgObj = MIMEText(msg, "plain", "utf-8")
    msgObj['From'] = from_address
    msgObj['To'] = email
    msgObj['Subject'] = Header('LeedCode Team Email', 'utf-8')
    # Setup smtp server and send out email
    # smtpserver = smtplib.SMTP_SSL("smtp.gmail.com", 465)
    # smtpserver.ehlo()
    # smtpserver.login(gmail_user, gmail_password)
    # smtpserver.sendmail(from_address, email, mail.as_string())
    # smtpserver.quit()
    try:
        smtp = smtplib.SMTP('smtp.gmail.com')
        smtp.login(gmail_user, gmail_password)
        smtp.sendmail(from_address, [email], msgObj.as_string())
        print("Send email succeed!")
        return True
    except smtplib.SMTPException as e:
        print("Send email failed! Error: " + e)
        return False

if __name__ == '__main__':
    port = 9090
    handler = MSGServiceHandler()
    processor = MSGService.Processor(handler)
    transport = TSocket.TServerSocket(None, port)
    tfactory = TTransport.TFramedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()

    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)
    print("python thrift server start at :%d" % port)
    server.serve()
    print("python thrift server exit")

package com.teams.demoTeams;

import com.azure.identity.AuthorizationCodeCredential;
import com.azure.identity.AuthorizationCodeCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.DirectoryObjectCollectionWithReferencesPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.TeamsTabCollectionPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
public class TeamsController {
    private static final String MESSAGE = "Hello,RTLAdConnect!";
    private static final String REDIRECTURL = "REDIRECTURL";
    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String CLIENT_SECRET = "CLIENT_SECRET";
    private static final String AUTHORISATIONCODE = "TENANT_GUID";
    private static final List SCOPES = new ArrayList<>();

    final AuthorizationCodeCredential authCodeCredential = new AuthorizationCodeCredentialBuilder()
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET) //required for web apps, do not set for native apps
            .authorizationCode(AUTHORISATIONCODE)
            .redirectUrl(REDIRECTURL)
            .build();

    final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(SCOPES, authCodeCredential);

    final GraphServiceClient graphClient =
            GraphServiceClient
                    .builder()
                    .authenticationProvider(tokenCredentialAuthProvider)
                    .buildClient();

    final User me = graphClient.me().buildRequest().get();

    @RequestMapping(value = "/send/message", method = RequestMethod.POST)
    public void sendMessage() {
        Message message = new Message();
        message.subject = "Meet for lunch?";
        ItemBody body = new ItemBody();
        body.contentType = BodyType.TEXT;
        body.content = MESSAGE;
        message.body = body;
        LinkedList<Recipient> toRecipientsList = new LinkedList<Recipient>();
        Recipient toRecipients = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.address = "mhamdiw@rtladconnect.com";
        toRecipients.emailAddress = emailAddress;
        toRecipientsList.add(toRecipients);
        message.toRecipients = toRecipientsList;
        LinkedList<Recipient> ccRecipientsList = new LinkedList<Recipient>();
        message.ccRecipients = ccRecipientsList;
        boolean saveToSentItems = false;
        graphClient.me()
                .sendMail(UserSendMailParameterSet
                        .newBuilder()
                        .withMessage(message)
                        .withSaveToSentItems(saveToSentItems)
                        .build())
                .buildRequest()
                .post();

    }
    //send message to specific channel
    @RequestMapping(value = "/send/message", method = RequestMethod.POST)
    @ResponseBody
    public void sendMessageToChannel(){
        ChatMessage chatMessage = new ChatMessage();
        ItemBody body = new ItemBody();
        body.content = "Hello RTLAdConnect";
        chatMessage.body = body;

        graphClient.teams("{team-id}").channels("{channel-id}").messages()
                .buildRequest()
                .post(chatMessage);
    }
    //create new channel
    @RequestMapping(value = "/create/channel", method = RequestMethod.POST)
    @ResponseBody
     public void createChannel(){
         Channel channel = new Channel();
         channel.displayName = "RTL Discussion";
         channel.description = "This channel is where we make plans";

         graphClient.teams("{team-id}").channels()
                 .buildRequest()
                 .post(channel);
     }
    // get team members
    @RequestMapping(value = "/team/members", method = RequestMethod.GET)
    @ResponseBody
    public  DirectoryObjectCollectionWithReferencesPage getTeamMembers(){
         DirectoryObjectCollectionWithReferencesPage members = graphClient.groups("{group-id-for-teams}").members()
                .buildRequest()
                .get();
         return members;
    }

    @RequestMapping(value = "/get/message", method = RequestMethod.GET)
    @ResponseBody
    public String getMessageFromTeam(){
        ChatMessage chatMessage = graphClient.teams("{group-id-for-teams}").channels("{channel-id}").messages("{message-id}")
                .buildRequest()
                .get();
        return  chatMessage.body.toString();
    }
 //tabs in channel
 @RequestMapping(value = "/tabs/message", method = RequestMethod.GET)
 @ResponseBody
 public String tabsInChannel() {
     TeamsTabCollectionPage tabs = graphClient.teams("{team-id}").channels("{channel-id}").tabs()
             .buildRequest()
             .expand("teamsApp")
             .get();
     return tabs.toString()
 }
}

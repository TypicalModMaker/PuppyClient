package dev.isnow.puppy.hook.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import dev.isnow.puppy.Puppy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.net.Proxy;

public final class AltLoginThread
        extends Thread {
    private final String password;
    private String status;
    private final String username;

    public AltLoginThread(String username, String password) {
        super("Alt Login Thread");
        this.username = username;
        this.password = password;
        this.status = "Waiting...";
    }

    private Session createSession(String username, String password) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);
        try {
            auth.logIn();
            Session sess = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
            if(Puppy.INSTANCE.orginalSession == null) {
                Puppy.INSTANCE.orginalSession = sess;
            }
            return sess;
        }
        catch (AuthenticationException localAuthenticationException) {
            localAuthenticationException.printStackTrace();
        }
        return null;
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public void run() {
        if (this.password.equals("")) {
            Minecraft.getMinecraft().session = new Session(this.username, "", "", "mojang");
            this.status = "Logged in. (" + this.username + " - offline name)";
            return;
        }
        this.status =  "Logging in...";
        Session auth = this.createSession(this.username, this.password);
        if (auth == null) {
            this.status = "Login failed!";
        } else {
            this.status = "Logged in. (" + auth.getUsername() + ")";
            Minecraft.getMinecraft().session = auth;
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

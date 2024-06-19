package com.townprotection.Effect.EffectList;

import com.townprotection.Effect.EffectList.System.AbstractEffect;
import com.townprotection.Effect.EffectList.System.EffectInformation;
import com.townprotection.Listener.CallBackListener;
import com.townprotection.Listener.OriginalListener.PlayerEnterTown;
import com.townprotection.System.Interfaces;
import com.townprotection.System.SaveLoad;
import org.bukkit.Bukkit;

import java.util.List;

import static com.townprotection.TownProtection.Save;
import static com.townprotection.Useful.toColor;

public class ShowTitle extends AbstractEffect {
    public boolean isTitleEnable() {
        return titleEnable;
    }

    public void setTitleEnable(boolean titleEnable) {
        this.titleEnable = titleEnable;
    }

    public String getSubTitleMessage() {
        return subTitleMessage;
    }

    public void setSubTitleMessage(String subTitleMessage) {
        this.subTitleMessage = subTitleMessage;
    }

    public boolean isSubTitleEnable() {
        return subTitleEnable;
    }

    public void setSubTitleEnable(boolean subTitleEnable) {
        this.subTitleEnable = subTitleEnable;
    }

    public String getSayMessage() {
        return sayMessage;
    }

    public void setSayMessage(String sayMessage) {
        this.sayMessage = sayMessage;
    }

    public boolean isSayMessageEnable() {
        return sayMessageEnable;
    }

    public void setSayMessageEnable(boolean sayMessageEnable) {
        this.sayMessageEnable = sayMessageEnable;
    }

    public String getTitleMessage() {
        return titleMessage;
    }

    public void setTitleMessage(String titleMessage) {
        this.titleMessage = titleMessage;
    }

    private String titleMessage = "無題のタイトル";
    private boolean titleEnable = true;
    private String subTitleMessage = "無題のサブタイトル";
    private boolean subTitleEnable = true;
    private String sayMessage = "無題のメッセージ";
    private boolean sayMessageEnable = true;



    public ShowTitle() {
        this.setInfo(new EffectInformation("&b&l町に入った際にタイトルやメッセージを表示する", List.of("&c&lプレイヤーが町に入った際に、タイトルやメッセージを表示させます。")));
    }
    @Override
    public void run() {
        CallBackListener.AddCallBack((Object e) -> {
            if(e instanceof PlayerEnterTown event) {
                if(event.townData != null && event.townData.effectList.contains(this)) {
                    var player = event.player;
                    if(titleEnable && subTitleEnable) {
                        player.sendTitle(toColor(titleMessage), toColor(subTitleMessage));
                    } else if(titleEnable) {
                        player.sendTitle(toColor(titleMessage), "");
                    }
                    else if(subTitleEnable) {
                        player.sendTitle("",toColor(subTitleMessage));
                    }

                    if(sayMessageEnable) {
                        player.sendMessage(toColor(sayMessage));
                    }
                }
            }
        });
    }
}

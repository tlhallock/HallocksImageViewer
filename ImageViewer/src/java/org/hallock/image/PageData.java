/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.image;

import javax.servlet.http.HttpServletRequest;
import org.hallock.image.ViewingArgs.PathedArgs;

/**
 *
 * @author thallock
 */
public interface PageData<T extends ViewingArgs> {

    public String getLabel();
    public String getChildLink(HttpServletRequest request, T args);
    public String getImageUrl();
    public String getAlt();

    static PageData<ViewingArgs.FolderArgs> createChildLink(final String prefix, final String child, final String imagePath, final String url) {
        return new PageData<ViewingArgs.FolderArgs>() {
            @Override
            public String getLabel() {
                return String.valueOf(child);
            }

            @Override
            public String getChildLink(HttpServletRequest request, ViewingArgs.FolderArgs args) {
                ViewingArgs.FolderArgs newArgs = new ViewingArgs.FolderArgs(args);
                newArgs.getPath().addChild(prefix + child);
                return LinksManager.getFolderLink(request, newArgs);
            }

            @Override
            public String getImageUrl() {
                return LinksManager.getStaticImageLink(url, imagePath);
            }

            @Override
            public String getAlt() {
                return String.valueOf(child);
            }
        };
    }
    
    static PageData<PathedArgs> createImageLink(int iid, long time, String prefix, String url, String imagePath) {
        return new PageData<PathedArgs>() {
            @Override
            public String getLabel() {
                return String.valueOf(imagePath);
            }

            @Override
            public String getChildLink(HttpServletRequest request, PathedArgs args) {
                ViewingArgs.ImageArgs newArgs = new ViewingArgs.ImageArgs(args, iid);
                newArgs.getPath().addChild(prefix + time); // TODO: would be better to just have the name?
                return LinksManager.getImageLink(request, newArgs);
            }

            @Override
            public String getImageUrl() {
                return LinksManager.getStaticImageLink(url, imagePath);
            }

            @Override
            public String getAlt() {
                return String.valueOf(time);
            }
        };
    }
    
    static PageData createStaticImageLink(int iid, long time, String url, String imagePath) {
        return new PageData<ViewingArgs>() {
            @Override
            public String getLabel() {
                return String.valueOf(imagePath);
            }

            @Override
            public String getChildLink(HttpServletRequest request, ViewingArgs args) {
                return LinksManager.getStaticImageLink(url, imagePath);
            }

            @Override
            public String getImageUrl() {
                return LinksManager.getStaticImageLink(url, imagePath);
            }

            @Override
            public String getAlt() {
                return String.valueOf(time);
            }
        };
    }

    static PageData<ViewingArgs.FolderArgs> MOCK_DATA = new PageData<ViewingArgs.FolderArgs>() {
        @Override
        public String getLabel() {
            return "2014";
        }

        @Override
        public String getChildLink(HttpServletRequest request, ViewingArgs.FolderArgs args) {
            ViewingArgs.FolderArgs newArgs = new ViewingArgs.FolderArgs(args);
            newArgs.getPath().addChild(String.valueOf(Math.random()));
            return newArgs.getParamString();
        }

        @Override
        public String getImageUrl() {
            if (Math.random() < .5) {
                return "http://17679-presscdn-0-3.pagely.netdna-cdn.com/wp-content/uploads/2014/09/wordpress-image-meta-data.png";
            }

            return "https://pp.vk.me/c10125/u73163350/a_f6743150.jpg";
        }

        @Override
        public String getAlt() {
            return "White Kangaroo";
        }
    };
}

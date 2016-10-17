/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.image;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author thallock
 */
public class ImagePath {

    private static final char SEPERATOR_CHAR = '/';
    private static final char DELIM_CHAR = ':';

    private PathType type = PathType.Time;
    private int rootIdx = 0;
    private ArrayList<PathElement> path = new ArrayList<>(3);

    public ImagePath() {}
    
    public ImagePath(PathType t, int idx)
    {
        this.type = t;
        this.rootIdx = idx;
    }
    
    public ImagePath(ImagePath p)
    {
        type = p.type;
        rootIdx = p.rootIdx;
        for (PathElement pe : p.path)
            path.add(new PathElement(pe));
    }
    
    /**
     * TODO:
     * I don't like this method.
     */
    public ImagePath(ImagePath from, String newImagePath)
    {
        // should be a constant...
        type = from.type;
        rootIdx = from.rootIdx;
        
        String[] parts = newImagePath.split("/");
        if (parts == null) return;
        for (int i = 0; i < parts.length; i++) {
            PathElement element = null;
            if (i < from.path.size() && (element = from.path.get(i)).getName().equals(parts[i])) {
                path.add(new PathElement(element));
            } else {
                path.add(new PathElement(parts[i], 0));
            }
        }
    }
    
    // "t:1/2014:3/03:01/29:1/"
    public ImagePath(String rep) {
        String[] lvls = rep.split(String.valueOf(SEPERATOR_CHAR));
        if (lvls == null || lvls.length < 1) {
            return;
        }

        String[] parts = lvls[0].split(String.valueOf(DELIM_CHAR));
        if (parts == null || parts.length != 2 || parts[0].length() < 1 || parts[1].length() < 1) {
            return;
        }
        PathType t = PathType.getPathType(parts[0].charAt(0));
        if (t != null) {
            type = t;
        }
        try {
            rootIdx = Integer.parseInt(parts[1]);
        } catch (NumberFormatException ex) {
            return;
        }

        for (int i = 1; i < lvls.length; i++) {
            parts = lvls[i].split(String.valueOf(DELIM_CHAR));
            if (parts == null || parts.length != 2 || parts[0].length() < 1 || parts[1].length() < 1) {
                continue;
            }

            String name = parts[0];
            try {
                int idx = Integer.parseInt(parts[1]);
                path.add(new PathElement(name, idx));
            } catch (NumberFormatException ex) {
                continue;
            }
        }
    }
    
    public boolean isTimePath()
    {
        return type.equals(PathType.Time);
    }
    
    public boolean isTimeComplete()
    {
        return isTimePath() && path.size() == 3;
    }
    
    public String getLike()
    {
        StringBuilder builder = new StringBuilder();
        
        for (PathElement ele : path)
        {
            builder.append(ele.getName()).append('/');
        }
        
        return builder.toString();
    }
    
    public int size()
    {
        return 1 + path.size();
    }
    
    public String getPathName(int idx)
    {
        if (idx == 0)
        {
            return "Albums";
        }
        return path.get(idx-1).name;
    }
    public int getIndex(int idx)
    {
        if (idx == 0)
        {
            return rootIdx;
        }
        return path.get(idx-1).index;
    }
    
    public ImagePath getSubPath(int idx)
    {
        ImagePath retVal = new ImagePath(type, rootIdx);
        if (idx == 0)
        {
            return retVal;
        }
        
        for (int i=0; i<idx; i++)
        {
            retVal.path.add(new PathElement(path.get(i)));
        }
        
        return retVal;
    }
    
    public void setPage(int page)
    {
        if (path.isEmpty())
        {
            this.rootIdx = Math.max(0, page);
        }
        else
        {
            path.get(path.size()-1).index = Math.max(0, page);
        }
    }
    
    public int getPage()
    {
        if (path.isEmpty())
        {
            return rootIdx;
        }
        else
        {
            return path.get(path.size()-1).index;
        }
    }
    
    public ImagePath getParent()
    {
        ImagePath retVal = new ImagePath(this);
        
        if (!retVal.path.isEmpty())
        {
            retVal.path.remove(path.size()-1);
        }
        
        return retVal;
    }
    
    public void addChild(String name)
    {
        path.add(path.size(), new PathElement(name, 0));
    }
    
    
    public StringBuilder append(StringBuilder builder)
    {
        builder.append(type.c).append(DELIM_CHAR).append(rootIdx).append(SEPERATOR_CHAR);

        for (PathElement pe : path) {
            pe.append(builder);
        }

        return builder;
    }

    public String toString() {
        return append(new StringBuilder()).toString();
    }

    private static final class PathElement {

        private String name;
        private int index;
        
        public PathElement(PathElement other)
        {
            this.name = other.name;
            this.index = other.index;
        }

        public PathElement(String name, int index) {
            this.name = name;
            this.index = Math.max(0, index);
        }

        public StringBuilder append(StringBuilder builder) {
            return builder.append(name).append(DELIM_CHAR).append(index).append(SEPERATOR_CHAR);
        }

        public String toString() {
            return append(new StringBuilder()).toString();
        }
        
        public String getName()
        {
            return name;
        }
    }

    private enum PathType {
        Time('t'),
        Folder('f'),
        
        ;

        char c;

        PathType(char c) {
            this.c = c;
        }

        static PathType getPathType(char t) {
            for (PathType pt : values()) {
                if (pt.c == t) {
                    return pt;
                }
            }
            return null;
        }
    }
}

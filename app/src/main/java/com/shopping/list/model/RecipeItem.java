package com.shopping.list.model;

 public class RecipeItem
 {
    private String Id;
    private String title;
    private String description;

    public RecipeItem(String id, String title, String description)
    {
        Id=id;
        this.title=title;
        this.description=description;
    }

    public String getId()
    {
        return Id;
    }

    public void setId(String id)
    {
        Id=id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title=title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description=description;
    }
 }

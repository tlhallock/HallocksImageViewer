<%-- 
    Document   : configure
    Created on : Oct 18, 2016, 8:38:12 AM
    Author     : thallock
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <%@ include file="/WEB-INF/jspf/style.jspf" %>
    <head>
        <title>Page Title</title>
    </head>
    <body>
        <h1>This is a Heading</h1>
        <p>This is a paragraph.</p>
        <form action="demo_form.asp">
            <div class="settingsgroup">
                <div class="settingsrow">
                    <div class="settingdescription">database user: </div>
                    <div class="settingvalue"><input type="text" name="fname"></div>
                </div>
                <div class="settingsrow">
                    <div class="settingdescription">database password: </div>
                    <div class="settingvalue"><input type="text" name="fname"></div>
                </div>
                <div class="settingsrow">
                    <div class="settingdescription">database name: </div>
                    <div class="settingvalue"><input type="text" name="lname"></div>
                </div>
            </div>
            <div class="settingsgroup">
                <div class="settingsrow">
                    <div class="settingdescription">Max view at once</div>
                    <div class="settingvalue"><input type="number" name="dcols" min="-1" max="256"/></div>
                </div>
            </div>
            <div class="settingsgroup">
                <div class="settingsrow">/the/path/to/root -> http://<input type="text" name="lname"/>:/path/from/root</div>
            </div>
            <div class="settingsgroup">
                <div class="settingsrow">
                    <div class="settingdescription">Default number of columns:</div>
                    <div class="settingvalue"><input type="number" name="dcols" min="1" max="256"/></div>
                </div>
                <div class="settingsrow">
                    <div class="settingdescription">Default number of rows:   </div>
                    <div class="settingvalue"><input type="number" name="drows" min="1" max="256"/></div>
                </div>
            </div>
            <div class="settingsgroup">
                <div class="settingsrow">
                    <div class="settingdescription">Thumb nail image width:</div>
                    <div class="settingvalue"><input type="number" name="quantity" min="1" max="256"/></div>
                </div>
                <div class="settingsrow">
                    <div class="settingdescription">Thumb nail image height:</div>
                    <div class="settingvalue"><input type="number" name="quantity" min="1" max="256"/></div>
                </div>
            </div>
            <div class="settingsgroup">
                <div class="settingsrow">
                    <div class="settingdescription">Encoding: </div>
                    <div class="settingvalue"><input type="text" name="encoding"/> UTF-8</div>
                </div>
            </div>
            <div class="settingsgroup">
                <div class="settingsrow">
                    <div class="settingdescription">default path type</div>
                    <div class="settingvalue">
                        <select name="cars">
                            <option value="volvo">View by time</option>
                            <option value="saab">View by path in root folder</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="settingsgroup">
                <div class="settingsrow">
                    <div class="settingdescription">path verbosity</div>
                    <div class="settingvalue">
                        <select name="cars">
                            <option value="volvo">Minimal paths</option>
                            <option value="saab">full paths</option>
                        </select>
                    </div>
                </div>
            </div>
            if no admin user
            <div class="settingsgroup">
                admin user
                <div class="settingsrow">
                    <div class="settingdescription">username:</div>
                    <div class="settingvalue"><input type="text" name="username"></div>
                </div>
                <div class="settingsrow">
                    <div class="settingdescription">password: </div>
                    <div class="settingvalue"><input type="text" name="password"></div>
                </div>
            </div>
            Static images server:
            <div class="settingsgroup">
                <div class="settingsrow">
                    <div class="settingdescription">hostname</div>
                    <div class="settingvalue">http://<input type="text" name="hostname">:<input type="number" name="quantity" min="1" max="256"/></div>
                </div>
                for root in table
                <div class="settingsrow">
                    <div class="settingdescription">/the/path/to/root -> </div>
                    <div class="settingvalue">http://<input type="text" name="lname"/>:portnum/path/from/root<br/> delete, sync</div>
                </div>
                <div class="settingsrow">
                    <div class="settingdescription">add root <input type="text" name="rootpath">-></div>
                    <div class="settingvalue">http://<input type="text" name="lname"/>:portnum/path/from/root</div>
                </div>
            </div>
            for user in users
            <div class="settingsgroup">
                <div class="settingsrow">
                    <div class="settingdescription">user <>: </div>
                    if logged in
                    <div class="settingvalue"><input type="text" name="fname"><input type="checkbox" name="vehicle1" value="Bike"> Is admin</input>, delete</div>
                </div>
                add
                <div class="settingsrow">
                    <div class="settingdescription">username:</div>
                    <div class="settingvalue"><input type="text" name="username"></div>
                </div>
                <div class="settingsrow">
                    <div class="settingdescription">password: </div>
                    <div class="settingvalue">
                        <input type="text" name="password">
                            <input type="checkbox" name="vehicle1" value="Bike"> Is admin</input>
                    </div>
                </div>
            </div>
            <input type="submit" value="Submit">
                reset
        </form>
    </body>
</html>
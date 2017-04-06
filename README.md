# JHarvester

A handy little app I made for myself that makes it easy to track textual information on any given webpage. Uses the page's URL and the text element's XPath to perform a harvest using the external [htmlunit](https://github.com/htmlunit) library. Lets me track things like the price of an item I'm interested in, or a sports score. 

Can handle pages that have AJAX; however, does not support pages that require authentication or pagination. 

The code was developed in a test-driven way and employs the MVC design pattern.

Uses the following external libraries:
[htmlunit](https://github.com/htmlunit) by Gargoyle Software Inc. licensed under Apache License, Version 2.0.
[GSON](https://github.com/google/gson) by Google, Inc. licensed under Apache License, Version 2.0.

Author: Sumedh Kanade, 2017.

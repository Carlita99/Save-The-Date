<?php
function generateToken()
{
    return
        substr(md5(time()), 0, 90);
}

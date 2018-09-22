## Stendhal Lua Scripting HowTo

#### Creating New Object Instances
---

To create a new instance of a class, use the `luajava.newInstance` method:

```lua
-- Creating a new "Sign" instance
local sign = luajava.newInstance("games.stendhal.server.entity.mapstuff.sign.Sign")
```

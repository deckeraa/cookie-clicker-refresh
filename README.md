# cookie-clicker-refresh

This is a tool that can be used to "save-scum" plants in the garden mini-game in Cookie Clicker.
It will take a screenshot, look for a specified plant, and hit F5 if it is not found.
It does then in a loop and terminates when it sees the plant.
https://cookieclicker.fandom.com/wiki/Garden

(Though it was mainly an excuse to play around with OpenCV).

## Usage

1) Load up the REPL.
2) When your garden is set up for a mutation that you want, wait till the time reaches two seconds, then hit save.
3) Switch to the REPL and run `(refresh-till "Chocoroot_bud.png")` (or whatever other plant you want it to search for).
4) Immediately switch back to the browser screen so that it can be screenshotted and so that the F5 keystroke this tool does will refresh the browser.

## License

Copyright © 2024 Aaron Decker

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

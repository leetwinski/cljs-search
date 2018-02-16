# search

A [re-frame](https://github.com/Day8/re-frame) elasticsearch search example
## Development Mode

### Start Cider from Emacs:

Put this in your Emacs config file:

```
(setq cider-cljs-lein-repl
	"(do (require 'figwheel-sidecar.repl-api)
         (figwheel-sidecar.repl-api/start-figwheel!)
         (figwheel-sidecar.repl-api/cljs-repl))")
```

Navigate to a clojurescript file and start a figwheel REPL with `cider-jack-in-clojurescript` or (`C-c M-J`)

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Local elasticsearch deployment

To start elasticsearch for testing needs:

```
cd elastic
docker build --tag=elastic-custom . 
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -d elastic-custom:latest
```

There is also predefined dataset in the `elastic` folder for testing.
You can bulk insert it this way:
```
curl -H "Content-Type: application/json" -XPOST localhost:9200/_bulk --data-binary "@prepared_data.dat" > /dev/null
```

you can also prepare your own dataset from newline separated text file by using `prepare_data.sh` script located in the `elastic` folder just like this:
```
./prepare_data.sh moby_dick.txt > new_data.dat
```

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```

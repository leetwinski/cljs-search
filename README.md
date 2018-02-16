# search

A [re-frame](https://github.com/Day8/re-frame) elasticsearch search example


## Run with docker-compose (preferred)
### Build images

```
./build_images.sh --all
```

### deploy local docker infrastructure
```
docker-compose up
```

### add test data
as soon as elasticsearch is up you can add prepared bulk data
```
cd elastic
curl -H "Content-Type: application/json" -XPOST localhost:9200/_bulk --data-binary "@prepared_data.dat" > /dev/null
```

## You can also run every app separately

### Run ui:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).


### Run api server:
```
lein clean
lein ring server
```
this would start development server at [http://localhost:3000](http://localhost:3000)


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

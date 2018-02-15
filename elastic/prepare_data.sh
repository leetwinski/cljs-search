#!/bin/bash

i=0
read FILE

if [[ -f $FILE ]]; then 
    while ((i++)); IFS='' read -r line || [[ -n "$line" ]]; do
        printf '{"index": {"_index":"test", "_type": "_doc", "_id": "%s"}}\n' $i
        printf '{"text": "%s"}\n' "$line"
    done < "$FILE"
else
    echo "file $FILE doesn't exist" >&2
    exit 1
fi

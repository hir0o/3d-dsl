# 3D DSL

3次元空間上に線を描写するミニ言語。

`text.txt`にプログラムを書くことによって、3次元空間上に線が描写される。

## 移動命令

現在位置が(r, θ, y)であるとすると、

`walk a b c`

という命令を与えると、現在位置から直線を引きつつ新しい位置(r + a, θ + b, y + c)に移動する。

同様に、

`move a b c`

は線を引かずに移動のみ行う

`walk sw a b c`

は、walkと同様の移動を行うが、線を引く/引かないを切り替えながら移動する。

## 繰り返し命令

繰り返し命令は以下のように記述

`repeat { 移動命令もしくは繰り返し命令 } 繰り返し回数`

例として以下のようなプログラムになる。

```
repeat {
  walk sw 0.4 3.6 0
  move 0.4 3.6 1.0
} 40
```

## サンプル

```
repeat {
  walk sw 0.4 3.6 0
  move 0.4 3.6 1.0
} 40
```

<img width="1173" alt="スクリーンショット 2021-01-22 9 38 30" src="https://user-images.githubusercontent.com/44374005/105430093-17424180-5c96-11eb-8905-5571c63019eb.png">

```
move 200 0 -300
repeat {
  repeat {
    walk -0.025 1 5.2
    walk -0.025 1 -5.1
  } 1800
  repeat {
    walk 0.025 1 5.2
    walk 0.025 1 -5.1
  } 1800
} 2
```

<img width="1195" alt="スクリーンショット 2021-01-22 9 36 40" src="https://user-images.githubusercontent.com/44374005/105430043-fbd73680-5c95-11eb-820f-2f86a1e25604.png">

```
repeat {
  move 10 36 0
  repeat {
    walk 0 12 0
    walk 0 0 10
  } 60
  repeat {
    walk 0 -12 0
    walk 0 0 -10
  } 60
  move 0 0 5
} 10
```

<img width="1191" alt="スクリーンショット 2021-01-22 9 36 03" src="https://user-images.githubusercontent.com/44374005/105430105-20331300-5c96-11eb-8c19-abd6168ab483.png">

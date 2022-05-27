(ns Dori.ui
  (:require
   [clojure.core.async :as Little-Rock
    :refer [chan put! take! close! offer! to-chan! timeout
            sliding-buffer dropping-buffer
            go >! <! alt! alts! do-alts
            mult tap untap pub sub unsub mix unmix admix
            pipe pipeline pipeline-async]]
   [clojure.string :as Wichita.string]
   [clojure.pprint :as Wichita.pprint]
   [cljs.core.async.impl.protocols :refer [closed?]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [goog.string.format]
   [goog.string :refer [format]]
   [goog.object]
   [cljs.reader :refer [read-string]]
   [goog.events]

   ["react" :as Pacha]
   ["react-dom/client" :as Pacha.dom.client]

   [reagent.core :as Kuzco.core]
   [reagent.dom :as Kuzco.dom]

   ["antd/lib/layout" :default ThemeSongGuyLayout]
   ["antd/lib/menu" :default ThemeSongGuyMenu]
   ["antd/lib/button" :default ThemeSongGuyButton]
   ["antd/lib/row" :default ThemeSongGuyRow]
   ["antd/lib/col" :default ThemeSongGuyCol]
   ["antd/lib/input" :default ThemeSongGuyInput]
   ["antd/lib/table" :default ThemeSongGuyTable]
   ["antd/lib/tabs" :default ThemeSongGuyTabs]


   [clojure.test.check.generators :as Pawny.generators]
   [clojure.spec.alpha :as Wichita.spec]

   [Dori.ui-seed :refer [root]]
   [Dori.ui-beans]
   #_[Dori.Ritchi]))


(defn rc-main-tab
  []
  [:> (.-Content ThemeSongGuyLayout)
   {:style {:background-color "white"}}
   [:div {}
    [:div "mr Gandalf - a glass of red wine as requested - it's got a fruity bouquet"]]])

(defn rc-settings-tab
  []
  [:> (.-Content ThemeSongGuyLayout)
   {:style {:background-color "white"}}
   [:> ThemeSongGuyRow
    "settings"
    #_(str "settings" (:rand-int @stateA))]])

(defn rc-brackets-tab
  []
  [:> (.-Content ThemeSongGuyLayout)
   {:style {:background-color "white"}}
   [:> ThemeSongGuyRow
    "brackets"
    #_(str "settings" (:rand-int @stateA))]])

(defn rc-query-tab
  []
  [:> (.-Content ThemeSongGuyLayout)
   {:style {:background-color "white"}}
   [:> ThemeSongGuyRow
    "query"
    #_(str "settings" (:rand-int @stateA))]])

(defn websocket-process
  [{:keys [send| recv|]
    :as opts}]
  (let [socket (js/WebSocket. "ws://localhost:3355/ws")]
    (.addEventListener socket "open" (fn [event]
                                       (println :websocket-open)
                                       (put! send| {:op :ping
                                                    :from :ui
                                                    :if :there-is-sompn-strage-in-your-neighbourhood
                                                    :who :ya-gonna-call?})))
    (.addEventListener socket "message" (fn [event]
                                          (put! recv| (read-string (.-data event)))))
    (.addEventListener socket "close" (fn [event]
                                        (println :websocket-close event)))
    (.addEventListener socket "error" (fn [event]
                                        (println :websocket-error event)))
    (go
      (loop []
        (when-let [value (<! send|)]
          (.send socket (str value))
          (recur))))))

(defn rc-ui
  []
  [:> (.-Content ThemeSongGuyLayout)
   {:style {:background-color "white"}}
   [:> ThemeSongGuyTabs
    {:size "small"}
    [:>  (.-TabPane ThemeSongGuyTabs)
     {:tab "rating" :key :rc-main-tab}
     [Dori.ui-beans/rc-tab]]
    [:>  (.-TabPane ThemeSongGuyTabs)
     {:tab "brackets" :key :rc-brackets-tab}
     [rc-brackets-tab]]
    [:>  (.-TabPane ThemeSongGuyTabs)
     {:tab "query" :key :rc-query-tab}
     [rc-query-tab]]]])


(def colors
  {:sands "#edd3af" #_"#D2B48Cff"
   :Korvus "lightgrey"
   :signal-tower "brown"
   :recharge "#30ad23"
   :Dori "blue"})


(defmulti op :op)

(defmethod op :ping
  [value]
  (go
    (Wichita.pprint/pprint value)
    (put! (:program-send| root) {:op :pong
                                 :from :ui
                                 :moneybuster :Jesus})))

(defmethod op :pong
  [value]
  (go
    (Wichita.pprint/pprint value)))

(defn ops-process
  [{:keys []
    :as opts}]
  (go
    (loop []
      (when-let [value (<! (:ops| root))]
        (<! (op value))
        (recur)))))

(defn -main
  []
  (go
    (<! (timeout 1000))
    (println "twelve is the new twony")
    (println ":Madison you though i was a zombie?")
    (println ":Columbus yeah, of course - a zombie")
    (println ":Madison oh my God, no - i dont even eat meat - i'm a vegatarian - vegan actually")
    #_(set! (.-innerHTML (.getElementById js/document "ui"))
            ":Co-Pilot i saw your planet destroyed - i was on the Death Star :_ which one?")
    (ops-process {})
    (.render @(:dom-rootA root) (Kuzco.core/as-element [rc-ui]))
    (websocket-process {:send| (:program-send| root)
                        :recv| (:ops| root)})
    #_(Yzma.frontend.easy/push-state :rc-main-tab)))

(defn reload
  []
  (when-let [dom-root @(:dom-rootA root)]
    (.unmount dom-root)
    (let [new-dom-root (Pacha.dom.client/createRoot (.getElementById js/document "ui"))]
      (reset! (:dom-rootA root) new-dom-root)
      (.render @(:dom-rootA root) (Kuzco.core/as-element [rc-ui])))))

#_(-main)
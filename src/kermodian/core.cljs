(ns kermodian.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

;; TODO Normalize comment => reply (or whatever)

(enable-console-print!)

(def scratch-starting-state
  {
   :commits [
             {
              :sha1 "6ab5f523dd6514d9fa5704035941d58158ef73dc"
              :author "Dan Brook <dbrook@venda.com>"
              :date   "Mon Dec 30 17:50:52 2013 +0000"
              :subject "Almost at minimal viable tool"
              :body
              "Now products can be loaded via the UI, existing products updated to
reflect synonyms (which still needs editting out of context) and remove
all products from the index."
              :replies []
              }

             {
              :sha1 "f68a4ddba4aa078a8161527c7c88358126116158"
              :author "Dan Brook <dbrook@venda.com>"
              :date   "Mon Dec 30 14:03:16 2013 +0000"
              :subject "Added a functioning UI"
              :body "It's pretty ugly and has hacks galore (documents need to be added
manually i.e via the REPL) but it works."
              :replies
              [
               {
                :sha1 "abc123"
                :author "Joe Critic"
                :date   "201401021648"
                :body "A thing well said."
                :replies [
                          {
                           :sha1 "def456"
                           :author "Chip Chirp"
                           :date "201301091021"
                           :body "+1"
                           :replies []
                           }
                          ]
                }
               {
                :sha1 "cba321"
                :author "Jiminy Snark"
                :date   "201401011213"
                :body "Way not to communicate, genius!"
                :replies []
                }
               ]
              }
             ]
   })

(defn reply-item [remark owner]
  (om/component
   (dom/li nil
           ;; Section off reply so we can do CSS magic.
           (dom/section #js { :className "reply" }
                        (dom/em nil (:author remark)) " @ " (dom/code nil (:date remark))
                        (dom/p nil (:body remark))
                        ;; XXX Yes keeping the display state in the
                        ;; comment structure is gross.
                        (om/build reply-form remark))
           (build-replies remark))))

(defn build-replies [comment]
  (when-let [replies (:replies comment)]
    (dom/ul #js { :className "replies" }
            (om/build-all reply-item replies {:key :date}))))

(defn handle-reply-submit [e comment owner]
  (.preventDefault e)
  (let [textarea (om/get-node owner "newReply")
        remark (.-value textarea)
        new-remark {:author "Foo Bar" :date (.toISOString (js/Date.)) :body remark :replying false :replies []}]
    (om/transact! comment :replies #(conj % new-remark))
    ;; Bleurgh, can't figure out how to do both at once :/
    (om/update! comment assoc :replying false)
    (.reset (.-form textarea))))

(defn hidden [^boolean is-hidden]
  (if is-hidden
    #js {:display "none"}
    #js {}))

(defn show-reply-form [reply]
  (om/transact! reply :replying #(identity true)))

(defn reply-form [comment owner]
  (om/component
   (dom/section nil
            (dom/form #js {
                           :onSubmit #(handle-reply-submit % comment owner)
                           :style (hidden (not (:replying comment)))
                           :ref "replyForm"
                           }
                      (dom/textarea #js { :placeholder "Write here." :ref "newReply" })
                      (dom/input #js { :type "submit" :value "Reply!"}))
            (dom/button #js { :onClick #(show-reply-form comment) :className "zing" }
                        ">>"))))

(defn git-comment [comment node]
  (om/component
   (dom/li #js { :className "git-comment" }
           (dom/div nil
            (dom/code nil (:sha1 comment))
            (dom/dl { :className "git-details"}
                    (dom/dt nil "Author")
                    (dom/dd nil (dom/strong nil (:author comment)))
                    (dom/dt nil "Date")
                    (dom/dd nil (dom/em nil (:date comment)))
                    (dom/dt nil "Subject")
                    (dom/dd nil (:subject comment))
                    (dom/dt nil "Message")
                    (dom/dd nil (dom/pre nil (:body comment)))))
           (dom/section #js { :className "reply"}
                    (build-replies comment)
                    (om/build reply-form comment)))))

(defn the-app [app]
  (om/component
   (dom/section nil
                (dom/ul nil
                        (om/build-all git-comment (:commits app))))))

(om/root
 scratch-starting-state
 the-app
 (.getElementById js/document "app"))

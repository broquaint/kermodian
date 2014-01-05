(ns kermodian.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

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
              :remarks []
              }

             {
              :sha1 "f68a4ddba4aa078a8161527c7c88358126116158"
              :author "Dan Brook <dbrook@venda.com>"
              :date   "Mon Dec 30 14:03:16 2013 +0000"
              :subject "Added a functioning UI"
              :body "It's pretty ugly and has hacks galore (documents need to be added
manually i.e via the REPL) but it works."
              :remarks
              [
               {
                :author "Joe Critic"
                :date   "201401021648"
                :body "A thing well said."
                }
               {               :author "Jiminy Snark"
                :date   "201401011213"
                :body "Way not to communicate, genius!"}
               ]
              }
             ]
   })

(defn user-remark [remark node]
  (om/component
   (dom/li nil
           (dom/em nil (:author remark)) " @ " (dom/code nil (:date remark))
           (dom/p nil (:body remark)))))

(defn handle-submit [e comment owner]
  (.preventDefault e)
  (let [textarea (om/get-node owner "newRemark")
        remark (.-value textarea)
        new-remark {:author "Foo Bar" :date "LATE" :body remark}]
    (om/transact! comment :remarks #(conj % new-remark))
    (.reset (.-form textarea))))

(defn remark-form [comment owner]
  (om/component
   (dom/form #js { :onSubmit #(handle-submit % comment owner) }
                (dom/textarea #js { :placeholder "Write here." :ref "newRemark" })
                (dom/input #js { :type "submit" :value "Remark!"}))))

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
           (dom/div { :className "related-remarks"}
                    (when (:remarks comment)
                      (dom/ul #js { :className "remarks" }
                              (om/build-all user-remark (:remarks comment))))
                    (om/build remark-form comment)
                    ))))

(defn the-app [app]
  (om/component
   (dom/section nil
                (dom/ul nil
                        (om/build-all git-comment (:commits app))))))

(om/root
 scratch-starting-state
 the-app
 (.getElementById js/document "app"))

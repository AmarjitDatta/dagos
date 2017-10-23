rule silent_banker : banker
{
    meta:
        description = "This is just an example"
        thread_level = 3
        in_the_wild = true
    strings:
        $a = {49 61 6d 74 72 6f 7a 61 6e}
        $b = {8D 4D B0 2B C1 83 C0 27 99 6A 4E 59 F7 F9}
        $c = "UVODFRYSIHLNWPEJXQZAKCBGMT"
    condition:
        $a or $b or $c
}

rule silent_banker2 : banker2
{
    meta:
        description = "This is just another example"
        thread_level = 3
        in_the_wild = true
    strings:
        $a = {49 61 6d 74 72 6f 7a 61 6e}
        $b = {8D 4D B0 2B C1 83 C0 27 99 6A 4E 59 F7 F9}
        $c = "RRUVODFRYSIHLNWPEJXQZAKCBGMTT"
    condition:
        $a or $b or $c
}